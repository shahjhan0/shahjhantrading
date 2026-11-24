# Shahjhan Mobile Trading Terminal — Android starter

This is a native Android Studio starter project using Kotlin and Jetpack Compose.

## Current prototype features
- Responsive mobile-first terminal shell
- Chart screen with illustrative candles
- Symbol and timeframe selectors
- Buy/Sell ticket preview buttons
- Lot, SL and TP input fields
- Bot manager screen and enable/disable UI toggle
- Trade, Account and Settings tabs

## Important limitations
This is a UI starter, not a live trading application:
- No broker connection or order execution is implemented.
- Chart values and account figures are illustrative placeholders.
- Selecting/importing `.mq5` or `.ex5` files is not implemented yet.
- Android cannot directly run desktop MT5 `.ex5` Expert Advisors. Existing `.mq5` strategy logic must be ported to a compatible Android execution engine, and a supported broker API/bridge must be selected.
- Do not use this prototype for live trading.

## Build
1. Open this folder in Android Studio (current stable version).
2. Allow Gradle sync.
3. Build > Build APK(s).
4. Install the generated debug APK on an Android device.

The project uses Android Gradle Plugin 8.7.3, Kotlin 2.0.21, Compose BOM 2024.12.01, compile SDK 35, min SDK 26.
