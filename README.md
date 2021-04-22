# java-repository-template

### How to finish setting up java repository

* Register repository to [codecov.io](https://app.codecov.io/). *Remember to add proper secret into your repository configuration*
* Create badge for code coverage. Copy from:`https://app.codecov.io/gh/<user>/<repository>>/settings/badge`
* Create badge for CI action 
* Template is prepared for Maven based project. Add JaCoCo to your project to properly generate coverage report.
* Fill proper username/app_name in `.github/workflows/release.yml` and add `DOCKERHUB_USERNAME` and `DOCKERHUB_TOKEN` secrets
```
![CI/CD](https://github.com/<user>/<repository>/actions/workflows/ci.yml/badge.svg)
```
