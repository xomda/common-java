import { defineConfig } from "vitepress";

// https://vitepress.dev/reference/site-config
export default defineConfig({
  title: "XOMDA",
  description: "Extensible Object Model Data Abstraction",

  head: [["link", { rel: "icon", href: "/logo.svg" }]],

  srcDir: "./docs",

  themeConfig: {
    // https://vitepress.dev/reference/default-theme-config

    nav: [
      { text: "Home", link: "/" },
      { text: "Documentation", link: "/intro/what-is-xomda" },
    ],

    sidebar: [
      {
        text: "Introduction",
        items: [{ text: "What is XOMDA?", link: "/intro/what-is-xomda" }],
      },
      {
        text: "Getting Started",
        items: [
          {
            text: "The Object Model",
            link: "/getting-started/the-object-model",
          },
          { text: "Templates", link: "/getting-started/templates" },
        ],
      },
      {
        text: "Usage",
        items: [{ text: "Using with Gradle", link: "/usage/gradle-build" }],
      },
    ],

    socialLinks: [{ icon: "github", link: "https://github.com/xomda/xomda" }],

    search: {
      provider: "local",
    },
  },
});
