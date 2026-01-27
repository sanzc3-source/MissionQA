FROM maven:3.9.9-eclipse-temurin-17

# Install Google Chrome
RUN apt-get update && apt-get install -y wget gnupg ca-certificates \
    && wget -qO- https://dl.google.com/linux/linux_signing_key.pub | gpg --dearmor > /usr/share/keyrings/google-linux-keyring.gpg \
    && echo "deb [arch=amd64 signed-by=/usr/share/keyrings/google-linux-keyring.gpg] http://dl.google.com/linux/chrome/deb/ stable main" \
        > /etc/apt/sources.list.d/google-chrome.list \
    && apt-get update && apt-get install -y google-chrome-stable \
    && rm -rf /var/lib/apt/lists/*

# Create non-root user + set proper HOME and Maven cache location
RUN useradd -m -u 1001 -s /bin/bash missionqa \
    && mkdir -p /app /app/artifacts /home/missionqa/.m2 \
    && chown -R missionqa:missionqa /app /home/missionqa

ENV HOME=/home/missionqa
ENV MAVEN_CONFIG=/home/missionqa/.m2

WORKDIR /app
COPY --chown=missionqa:missionqa pom.xml .
COPY --chown=missionqa:missionqa src ./src

USER missionqa

# Warm cache (now writes to /home/missionqa/.m2)
RUN mvn -q -DskipTests dependency:go-offline

ENV BROWSER=chromeheadless
ENV TAGS=""

CMD ["bash", "-lc", "mvn clean test -Dcucumber.filter.tags=\"${TAGS}\""]
