FROM ghcr.io/cirruslabs/android-sdk:36

WORKDIR /workspace
COPY . .

RUN chmod +x ./gradlew && ./gradlew --no-daemon --console=plain :apps:mobile-android:assembleDebug

CMD ["sh", "-c", "find apps/mobile-android/build/outputs -type f -name '*.apk' -print"]
