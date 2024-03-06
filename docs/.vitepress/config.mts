import { defineConfig } from "vitepress";

// https://vitepress.dev/reference/site-config
export default defineConfig({
  title: "XOMDA / common-java",
  description: "Common Java utils",

  head: [["link", { rel: "icon", href: "/logo.svg" }]],

  srcDir: "./docs",

  themeConfig: {

    nav: [
      { text: "Home", link: "http://xomda.org" , target: "_self" },
      { text: "Documentation", link: "/exception/sneaky-throw" },
    ],

    sidebar: [
      {
        text: "Exception",
        items: [
            { text: "SneakyThrow", link: "/exception/sneaky-throw" }
        ],
      },
      {
        text: "Function",
        items: [
            { text: "BufferedStream", link: "/function/buffered-stream" },
            { text: "Predicates", link: "/function/predicates" }
        ],
      },
    ],

    socialLinks: [{ icon: "github", link: "https://github.com/xomda/common-java" }],

    search: {
      provider: "local",
    },
  },
});
