/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ["./app/**/*.{js,jsx,ts,tsx}", "./src/**/*.{js,jsx,ts,tsx}"],
  presets: [require("nativewind/preset")],
  theme: {
    extend: {
      colors: {
        primary: {
          DEFAULT: "#21226b",
          dark: "#1a1b56",
          light: "#3a3ba0",
        },
        lightBlue: "#E6F4FE",
        greyText: "#8a8a8a",
      },
    },
  },
  plugins: [],
};