FROM ghcr.io/cirruslabs/android-sdk:36

WORKDIR /workspace
COPY . .

RUN chmod +x ./gradlew && ./gradlew --no-daemon --console=plain :apps:mobile-kmp:check

CMD ["sh", "-c", "find apps/mobile-kmp/build -maxdepth 4 -type f | sort | head -50"]
