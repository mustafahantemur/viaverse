/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import { useState } from "react";
import { AnimatePresence, motion } from "motion/react";
import SplashScreen from "./components/SplashScreen";
import LandingScreen from "./components/LandingScreen";

export default function App() {
  const [showSplash, setShowSplash] = useState(true);

  return (
    <>
      <LandingScreen onLogout={() => setShowSplash(true)} />
      
      <AnimatePresence>
        {showSplash && (
          <SplashScreen key="splash" onComplete={() => setShowSplash(false)} />
        )}
      </AnimatePresence>
    </>
  );
}
