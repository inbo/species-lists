import {
  Footer,
  Header,
  IndigenousAcknowledgement,
} from "@atlasoflivingaustralia/ala-mantine";
import { useEffect } from "react";

// Navigation
import {
  NavigationProgress,
  completeNavigationProgress,
  resetNavigationProgress,
  startNavigationProgress,
} from "@mantine/nprogress";

// Routing
import { Outlet, useNavigation } from "react-router";

// Authentication
import { ExternalBanner } from "#/components/ExternalBanner";

function Dashboard() {
  const { state } = useNavigation();

  // Effect handler for navigation process indicator
  useEffect(() => {
    if (state === "loading") {
      resetNavigationProgress();
      startNavigationProgress();
    } else {
      completeNavigationProgress();
    }
  }, [state]);

  return (
    <>
      <NavigationProgress
        stepInterval={20}
        aria-label="Navigation progress bar"
        portalProps={{ "aria-hidden": true }}
      />
      <ExternalBanner
        url={import.meta.env.VITE_ALA_MESSAGES}
        services={["species-lists"]} // add `'test-warning'` to services array to test the warning banner
      />
      <Header
        homeUrl={import.meta.env.VITE_ALA_HOME_PAGE || ""}
        onSearchClick={() => (window.location.href = "https://bie.ala.org.au")}
        myProfileUrl={import.meta.env.VITE_ALA_USER_PROFILE || ""}
      />
      <Outlet />
      <Footer fullWidth />
      <IndigenousAcknowledgement />
    </>
  );
}

export default Dashboard;
