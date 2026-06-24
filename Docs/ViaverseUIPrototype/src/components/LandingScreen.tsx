import { useState } from "react";
import AuthScreen from "./AuthScreen";
import RegisterScreen from "./RegisterScreen";
import HomeScreen from "./HomeScreen";
import ChatScreen from "./ChatScreen";
import ChatHistoryScreen from "./ChatHistoryScreen";
import ProfileScreen from "./ProfileScreen";
import PublicProfileScreen from "./PublicProfileScreen";
import ProviderWelcomeScreen from "./ProviderWelcomeScreen";
import JobsScreen from "./JobsScreen";

export default function LandingScreen({ onLogout }: { onLogout?: () => void }) {
  const [step, setStep] = useState<"auth" | "register" | "home" | "chat_history" | "chat" | "profile" | "public_profile" | "jobs" | "provider_welcome">("auth");
  const [publicProfileUser, setPublicProfileUser] = useState<any>(null);

  const handleOpenPublicProfile = (user: any) => {
    setPublicProfileUser(user);
    setStep("public_profile");
  };

  if (step === "provider_welcome") {
    return <ProviderWelcomeScreen onBack={() => setStep("profile")} onStart={() => setStep("home")} />;
  }

  if (step === "public_profile" && publicProfileUser) {
    return <PublicProfileScreen user={publicProfileUser} onBack={() => setStep("home")} />;
  }

  if (step === "chat") {
    return <ChatScreen onBack={() => setStep("chat_history")} />;
  }

  if (step === "chat_history") {
    return (
      <ChatHistoryScreen 
        onBack={() => setStep("home")} 
        onChat={() => setStep("chat")} 
        onHome={() => setStep("home")}
        onJobs={() => setStep("jobs")}
        onProfile={() => setStep("profile")}
      />
    );
  }

  if (step === "profile") {
    return (
      <ProfileScreen 
        onBack={() => setStep("home")} 
        onHome={() => setStep("home")} 
        onJobs={() => setStep("jobs")}
        onMessages={() => setStep("chat_history")}
        onOpenPublicProfile={handleOpenPublicProfile} 
        onProviderWelcome={() => setStep("provider_welcome")}
        onLogout={() => {
          setStep("auth");
          if (onLogout) onLogout();
        }}
      />
    );
  }

  if (step === "jobs") {
    return (
      <JobsScreen 
        onHome={() => setStep("home")} 
        onChat={() => setStep("chat_history")} 
        onProfile={() => setStep("profile")}
        onOpenPublicProfile={handleOpenPublicProfile} 
      />
    );
  }

  if (step === "home") {
    return <HomeScreen onBack={() => setStep("auth")} onChat={() => setStep("chat_history")} onProfile={() => setStep("profile")} onOpenPublicProfile={handleOpenPublicProfile} onJobs={() => setStep("jobs")} />;
  }

  if (step === "register") {
    return <RegisterScreen onBack={() => setStep("auth")} onClose={() => setStep("home")} />;
  }

  return (
    <AuthScreen 
      onContinue={() => setStep("register")} 
      onSkip={() => setStep("home")} 
    />
  );
}

