import { defineConfig } from "vitepress";

// https://vitepress.dev/reference/site-config
export default defineConfig({
  title: "XOMDA / common-java",
  description: "Common Java utils",

  head: [["link", { rel: "icon", href: "/logo.svg" }]],

  srcDir: "./docs",

  themeConfig: {
    nav: [
      { text: "Home", link: "http://xomda.org", target: "_self" },
      { text: "Documentation", link: "/exception/sneaky-throw" },
      {
        text: "Utils",
        items: [
          {
            text: "common-java",
            target: "_self",
            link: "//common-java.xomda.org/",
          },
        ],
      },
    ],

    sidebar: [
      {
        text: "exception",
        items: [{ text: "SneakyThrow", link: "/exception/sneaky-throw" }],
      },
      {
        text: "function",
        items: [{ text: "Predicates", link: "/function/predicates" }],
      },
      {
        text: "utils.stream",
        items: [
          { text: "BufferedStream", link: "/utils/stream/buffered-stream" },
        ],
      },
    ],

    socialLinks: [
      { icon: "github", link: "https://github.com/xomda/common-java" },
    ],

    search: {
      provider: "local",
    },
  },
});
