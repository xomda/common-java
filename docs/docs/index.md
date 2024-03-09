---
# https://vitepress.dev/reference/default-theme-home-page
layout: home

hero:
  name: "XOMDA"
  text: "Tools for Data Abstraction"
  tagline: Don't write it. Generate it!
  image: "./logo-anim.svg"
  actions:
    - theme: brand
      text: Get Started
      link: /intro/what-is-xomda
    - theme: alt
      text: Test Project
      link: https://github.com/xomda/test-project

features:
  - title: CSV Model
    details: Define your object model in CSV format.
  - title: Code Generation
    details: Parse the object model CSV into (a custom) AST and generate code from it.
  - title: Extensible
    details: Customise the way models are parsed and how code is generated.
  - title: Gradle Integration
    details: Easily integrate the whole process into your gradle build.
---

<script setup>
import { VPButton } from 'vitepress/theme'
</script>

<div style="margin: 1.25em; margin-top:2.5em; text-align: center;">
<VPButton text="♥️ Buy us a coffee" href="https://ko-fi.com/xomda" theme="sponsor" style="text-decoration: none"/>
</div>
