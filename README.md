# Model Router

![Java](https://img.shields.io/badge/Java-27-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.1-brightgreen)
![Spring AI](https://img.shields.io/badge/Spring%20AI-2.0.1-6DB33F)
![Maven](https://img.shields.io/badge/Maven-build-C71A36)
![JaCoCo](https://img.shields.io/badge/coverage-JaCoCo-F58A03)

A Spring Boot service that routes chat prompts to the cheapest Claude model tier (Haiku, Sonnet, Opus, or Fable) capable of answering them well. Each incoming prompt is first classified by a routing decision step (built on Spring AI and TypeSafe AI), then forwarded to the selected Anthropic model via Spring AI's `ChatClient`, balancing response quality against cost.
