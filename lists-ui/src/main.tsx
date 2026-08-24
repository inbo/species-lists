// Global styles
import "@atlasoflivingaustralia/ala-mantine/styles";
import "@mantine/core/styles.css";
import "@mantine/notifications/styles.css";
import "@mantine/nprogress/styles.css";

import {
  theme,
  VBPIntlProviderWrapper,
  VBPAuthProviderWrapper,
  defaultMessages,
} from "@atlasoflivingaustralia/ala-mantine";
import { ColorSchemeScript, MantineProvider } from "@mantine/core";
import { ModalsProvider } from "@mantine/modals";
import { Notifications } from "@mantine/notifications";
import notificationStyles from "./Notifications.module.css";
import { CookiesProvider } from "react-cookie";

// Authentication
import { ALAProvider } from "./helpers/context/ALAProvider";

// Internationalization
import nl from "./locale/nl.json";

// Application
import App from "./App";

// `domain` is used as the silent-renew redirect target, so it must be absolute.
const APP_URL = new URL(import.meta.env.BASE_URL, window.location.origin).href;

function Main() {
  return (
    <CookiesProvider>
      <VBPAuthProviderWrapper
        domain={APP_URL}
        authority={import.meta.env.VITE_AUTH_AUTHORITY}
        clientId={import.meta.env.VITE_AUTH_CLIENT_ID}
        scope={import.meta.env.VITE_AUTH_SCOPE}
        authCookieDomain={import.meta.env.VITE_AUTH_COOKIE_DOMAIN}
      >
        <ColorSchemeScript defaultColorScheme="auto" />
        <MantineProvider theme={theme} defaultColorScheme="auto">
          <VBPIntlProviderWrapper
            initialMessages={{ ...defaultMessages.nl, ...nl }}
            messagesLoader={async (loc: "en" | "nl") =>
              loc === "nl"
                ? { ...defaultMessages.nl, ...nl }
                : {
                    ...defaultMessages.en,
                    ...(await import("./locale/en.json")).default,
                  }
            }
          >
            <ModalsProvider modalProps={{ radius: "lg" }}>
              <ALAProvider>
                <Notifications
                  transitionDuration={400}
                  position="top-right"
                  classNames={notificationStyles}
                />
                <App />
              </ALAProvider>
            </ModalsProvider>
          </VBPIntlProviderWrapper>
        </MantineProvider>
      </VBPAuthProviderWrapper>
    </CookiesProvider>
  );
}

export default Main;
