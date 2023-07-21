const express = require('express');
const app = express();
const { getSenders, addSender, removeSender, updateSenderWorldwide, updateSenderSafety, getOptimalSender, updateSenderLastSent, resetSendersLoad } = require("./Database")
const secret = "YOUR_SECRET";
const admin = require("firebase-admin");
const libphonenumber = require('libphonenumber-js');

const serviceAccount = require("./admin.json");
admin.initializeApp({
    credential: admin.credential.cert(serviceAccount),
});

app.use(express.json());

function authenticate(req, res, next) {
    const token = req.body.secret;
    if (token != secret)
        return res.status(200).json({ ok: false });
    next();
}

app.post('/get-senders', authenticate, async (req, res) => {
    return res.status(200).json(await getSenders());
});

app.post('/add-sender', authenticate, async (req, res) => {
    return res.status(200).json(await addSender(req.body));
});

app.post('/update-sender-worldwide', authenticate, async (req, res) => {
    return res.status(200).json(await updateSenderWorldwide(req.body));
});

app.post('/update-sender-safety', authenticate, async (req, res) => {
    return res.status(200).json(await updateSenderSafety(req.body));
});

app.post('/remove-sender', authenticate, async (req, res) => {
    return res.status(200).json(await removeSender(req.body));
});

resetSendersLoad();
setInterval(resetSendersLoad, 24 * 3600 * 1000)

app.post('/send-sms', authenticate, async (req, res) => {
    let iso2 = "";
    const { phone_number, text } = req.body
    try { iso2 = libphonenumber.parsePhoneNumber(phone_number).country.toLowerCase() } catch (e) { }
    const res_sender = await getOptimalSender(iso2);
    if (!res_sender)
        return res.status(200).json({ ok: false });

    const { id, sim_name, firebase_token, safety, last_sent } = res_sender
    let message = {
        data: { phone_number, sim_name, text, password: secret },
        token: firebase_token
    };

    const nowtime = Date.now();
    if (safety && last_sent + 6000 > nowtime) {
        const delay = last_sent + 6000 - nowtime;
        await new Promise((resolve) => setTimeout(resolve, delay));
    }
    updateSenderLastSent({ id, last_sent: Date.now() })
    admin.messaging().send(message)
        .then(async () => {
            return res.status(200).json({ ok: true });
        })
        .catch((e) => {
            return res.status(200).json({ ok: false });
        });
})

app.listen(80);