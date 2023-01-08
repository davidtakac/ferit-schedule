# Ferit Schedule - Student app

A wrapper app for [Ferit's](https://www.ferit.unios.hr) online class schedule.
[Latest version - on Play Store](https://play.google.com/store/apps/details?id=os.dtakac.feritraspored)

## Contributions
Contributions are very welcome and encouraged! You can contribute by: 
- Creating a **pull request** for the `dev` branch.
- Creating **issues**. Describe what you were doing when an issue occurred and what you expected to 
happen instead. 
- Sending an **email** to developer.takac@gmail.com. This includes written feature requests or bug
reports. 

## How it works - in a nutshell
1. Download HTML of schedule web-page for given settings
2. Extract data from HTML (title)
3. De-bloat (remove scripts and unneeded elements)
4. Apply transformations (highlights, time on blocks and dark theme)
5. Display in WebView

## Features
- Remove scripts and unneeded elements
- Automatically load current week and scroll to current day for your study programme and year
- Switch between Croatian and English schedule language in-app
- Display class period on its block
- Skip days after specified time
- Skip saturdays
- Highlight blocks based on filter
- Dark and light theme

## Code features
- Model-View-ViewModel
- View binding
- Single-activity, multi-fragment
- ViewModel and LiveData for configuration changes (rotation and theme)
- Navigation components
- Koin dependency injection
- Kotlin coroutines for network calls
- Kotlin property delegates
- Android preferences library
- Native day and night mode
- Material design
- Core library de-sugaring for that sweet Java 8 time

## Credit
To my friends:  

- [Robert Sorić](https://rsoric.github.io/) for the initial highlight code
- [Luka Šimić](https://github.com/lsimic) for helping me with CSS and general web problems
- [Tomislav Rekić](https://github.com/tomislavrekic) for alpha-testing and design help
- Antonio Firić for alpha-testing  

Who I thank for their support and feedback.