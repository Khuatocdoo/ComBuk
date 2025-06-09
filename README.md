# ComBuk

Android lunch ordering application

## Main Features
- Place orders: Select dishes, quantity, and confirm your order.
- Inventory display: View available dishes, remaining quantity, and price.
- Data management using JSON files (menu, orders).

## Project Structure
- `app/src/main/assets/menu.json`: List of dishes, quantity, and price.
- `app/src/main/assets/orders.json`: List of placed orders.
- `app/src/main/java/com/example/combuk/DatabaseManager.kt`: Manage reading/writing JSON data.
- `app/src/main/java/com/example/combuk/OrderActivity.kt`: UI for ordering, selecting quantity, and confirming.
- `app/src/main/java/com/example/combuk/InventoryActivity.kt`: Display inventory of dishes.
- `app/src/main/java/com/example/combuk/OrderAdapter.kt`, `InventoryAdapter.kt`: RecyclerView adapters.
- `app/src/main/res/layout/`: UI layout files.

## Usage Guide
1. Run the app in Android Studio.
2. Place orders in the Order screen, select quantity, and confirm.
3. View inventory in the Inventory screen.
4. Order data is saved to the `orders.json` file in the app's internal storage.

## Notes
- JSON files in assets are sample data; the app writes actual data to the internal storage (filesDir).
- To view the real orders.json file, use Device File Explorer or adb pull.
