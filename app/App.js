import { useEffect, useState } from "react";
import Dashboard from "./screens/Dashboard";

export default function App() {
  global.addr = "SERVER_ADDRESS";
  global.secret = "YOUR_SECRET";
  return <Dashboard></Dashboard>
}