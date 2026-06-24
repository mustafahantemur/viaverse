import { motion, useMotionValue, useSpring, useMotionTemplate, useTransform } from "motion/react";
import React, { useMemo, useState, useEffect } from "react";
import viaverseLogo from "../icons/viaverse_v_orange_green.png";
import viaverseText from "../icons/viaverse_text.png";

interface SplashScreenProps {
  onComplete: () => void;
  key?: string;
}

const Particle: React.FC<{ pointerX: any, pointerY: any }> = ({ pointerX, pointerY }) => {
  const initVars = useMemo(() => {
    return {
      offsetX: (Math.random() - 0.5) * (typeof window !== "undefined" ? window.innerWidth : 1000) * 1.5,
      offsetY: (Math.random() - 0.5) * (typeof window !== "undefined" ? window.innerHeight : 1000) * 1.5,
      stiffness: Math.random() * 20 + 5,
      damping: Math.random() * 10 + 10,
      size: Math.random() * 4 + 2,
      duration: Math.random() * 15 + 10, // Slower duration for falling
      floatX: (Math.random() - 0.5) * 80, // Horizontal sway
      floatY: window.innerHeight * 0.8 + Math.random() * 400 // Fall distance
    };
  }, []);

  const [isMounted, setIsMounted] = useState(false);
  useEffect(() => setIsMounted(true), []);

  const targetX = useTransform(pointerX, (v: number) => v + initVars.offsetX);
  const targetY = useTransform(pointerY, (v: number) => v + initVars.offsetY);

  const springX = useSpring(targetX, { stiffness: initVars.stiffness, damping: initVars.damping });
  const springY = useSpring(targetY, { stiffness: initVars.stiffness, damping: initVars.damping });

  if (!isMounted) return null;

  return (
    <motion.div
      className="absolute pointer-events-none z-0"
      style={{
        x: springX,
        y: springY,
        left: 0,
        top: 0
      }}
    >
      <motion.div
        className="bg-orange-500 opacity-80 rounded-full"
        style={{
          width: initVars.size,
          height: initVars.size,
        }}
        animate={{
          x: [0, initVars.floatX, 0],
          y: [-100, initVars.floatY],
          opacity: [0, 0.7, 0.7, 0]
        }}
        transition={{
          duration: initVars.duration,
          repeat: Infinity,
          ease: "linear"
        }}
      />
    </motion.div>
  );
};

export default function SplashScreen({ onComplete }: SplashScreenProps) {
  const pointerX = useMotionValue(typeof window !== "undefined" ? window.innerWidth / 2 : 0);
  const pointerY = useMotionValue(typeof window !== "undefined" ? window.innerHeight / 2 : 0);
  
  // Keep the mask radius small and blurry. Base radius = 80px, larger on touch.
  const maskSize = useSpring(0, { stiffness: 100, damping: 20 });
  const maskImage = useMotionTemplate`radial-gradient(circle ${maskSize}px at ${pointerX}px ${pointerY}px, transparent 0%, rgba(0,0,0,0.1) 10%, rgba(0,0,0,0.6) 40%, black 100%)`;

  const [isActive, setIsActive] = useState(false);
  const [startPos, setStartPos] = useState({ x: 0, y: 0 });

  useEffect(() => {
    maskSize.set(0); // No opening until interaction
  }, [maskSize]);

  const handlePointerDown = (e: React.PointerEvent) => {
    setIsActive(true);
    maskSize.set(e.pointerType === 'touch' ? 70 : 80);
    setStartPos({ x: e.clientX, y: e.clientY });
    pointerX.set(e.clientX);
    pointerY.set(e.clientY);
  };

  const handlePointerMove = (e: React.PointerEvent) => {
    pointerX.set(e.clientX);
    pointerY.set(e.clientY);
    if (e.pointerType === 'mouse' && !isActive) {
      maskSize.set(40);
    }
  };

  const handlePointerUp = (e: React.PointerEvent) => {
    setIsActive(false);
    maskSize.set(e.pointerType === 'mouse' ? 40 : 0);
    
    const dx = e.clientX - startPos.x;
    const dy = e.clientY - startPos.y;
    const dist = Math.sqrt(dx * dx + dy * dy);
    
    if (dist < 15) {
      onComplete(); // Complete when clicking/tapping without dragging too much
    }
  };

  const handlePointerLeave = () => {
    maskSize.set(0);
    setIsActive(false);
  };

  // 4 times more particles for better rain effect
  const particleIds = useMemo(() => Array.from({ length: 240 }, (_, i) => i), []);

  return (
    <motion.div 
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      exit={{ opacity: 0 }}
      transition={{ duration: 0.5 }}
      onPointerDown={handlePointerDown}
      onPointerMove={handlePointerMove}
      onPointerUp={handlePointerUp}
      onPointerCancel={handlePointerUp}
      onPointerLeave={handlePointerLeave}
      style={{
        WebkitMaskImage: maskImage,
        maskImage: maskImage,
      }}
      className="fixed inset-0 z-[999] h-[100dvh] w-screen flex flex-col items-center justify-center bg-orange-100/99 overflow-hidden touch-none cursor-crosshair"
    >
      <div className="absolute inset-0 z-0 pointer-events-none opacity-60">
        <motion.div
          animate={{ scale: [1, 1.2, 1], opacity: [0.3, 0.5, 0.3] }}
          transition={{ duration: 6, repeat: Infinity, ease: "easeInOut" }}
          className="absolute w-[400px] h-[400px] bg-orange-400/30 blur-[90px] rounded-full top-1/4 left-1/3 -translate-x-1/2 -translate-y-1/2"
        />
        
        <motion.div
          animate={{ scale: [1, 1.3, 1], opacity: [0.2, 0.4, 0.2] }}
          transition={{ duration: 8, delay: 1, repeat: Infinity, ease: "easeInOut" }}
          className="absolute w-[500px] h-[500px] bg-amber-500/20 blur-[80px] rounded-full bottom-1/4 right-1/4 translate-x-1/4"
        />

        {particleIds.map(id => (
          <Particle key={id} pointerX={pointerX} pointerY={pointerY} />
        ))}
      </div>

      <div className="flex flex-col items-center justify-center relative z-10 pointer-events-none drop-shadow-2xl">
        <motion.div
           initial={{ opacity: 0, scale: 0.5, y: 15 }}
           animate={{ opacity: 1, scale: 1, y: 0 }}
           transition={{ duration: 0.6, type: "spring", bounce: 0.4 }}
           className="mb-8 flex items-center justify-center"
        >
          <img 
            src={viaverseLogo} 
            alt="Viaverse Logo" 
            className="w-48 h-48 md:w-64 md:h-64 object-contain" 
          />
        </motion.div>
        
        <div className="flex items-center justify-center w-full px-6">
          <motion.div
            initial={{ y: 30, opacity: 0 }}
            animate={{ y: 0, opacity: 1 }}
            transition={{ delay: 0.2, duration: 0.6, ease: "easeOut" }}
            className="flex items-center justify-center w-full"
          >
            <img 
              src={viaverseText} 
              alt="Viaverse Text" 
              className="w-full max-w-[260px] md:max-w-[340px] drop-shadow-md object-contain" 
            />
          </motion.div>
        </div>
      </div>
    </motion.div>
  );
}
