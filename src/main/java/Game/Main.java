package Game;

import Exception.*;
import Item.*;
import java.util.ArrayList;
import java.util.List;
import Order.*;
import Recipe.Recipe;

public class Main {
    public static void main(String[] args) throws InvalidDataException {
        try {
            System.out.println("=== 1. SETUP RESEP & ORDER MANAGER ===");
            OrderManager orderManager = new OrderManager();

            // --- Membuat Definisi Resep: "Sushi Set" ---
            // Kita butuh: Nasi (Cooked), Ikan (Chopped), Nori (Raw)
            
            // 1. Requirement Nasi Matang
            Rice reqRice = new Rice("Nasi");
            reqRice.setState(IngredientState.COOKED);

            // 2. Requirement Ikan Potong
            Fish reqFish = new Fish("Ikan");
            reqFish.chop(); // Set state jadi CHOPPED

            // 3. Requirement Nori (Nori selalu ready/raw di code kamu)
            Nori reqNori = new Nori("Nori");

            List<Ingredient> recipeIngredients = new ArrayList<>();
            recipeIngredients.add(reqRice);
            recipeIngredients.add(reqFish);
            recipeIngredients.add(reqNori);

            Recipe sushiRecipe = new Recipe("Sushi Set", recipeIngredients);
            System.out.println("Resep terdaftar: " + sushiRecipe.getName());


            // --- Membuat Order ---
            // Reward 100, Penalty 50, Waktu 60 detik
            Order currentOrder = orderManager.createOrder(sushiRecipe, 100, 50, 60);
            System.out.println("New Order Created: " + currentOrder);


            System.out.println("\n=== 2. SIMULASI CHEF MEMASAK (PREPARING) ===");
            
            // Chef mengambil bahan mentah
            Rice chefRice = new Rice("Nasi"); // Masih RAW
            Fish chefFish = new Fish("Ikan"); // Masih RAW
            Nori chefNori = new Nori("Nori"); // Ready

            System.out.println("Status Awal Nasi: " + chefRice.getState());
            
            // --- Proses Memasak Nasi ---
            // Ingat logic Rice.java: Raw -> Cooking -> Cooked
            System.out.println(">> Memasak Nasi...");
            chefRice.cook(); // Jadi COOKING
            chefRice.cook(); // Jadi COOKED
            System.out.println("Status Nasi Sekarang: " + chefRice.getState());

            // --- Proses Memotong Ikan ---
            System.out.println("Status Awal Ikan: " + chefFish.getState());
            System.out.println(">> Memotong Ikan...");
            chefFish.chop(); // Jadi CHOPPED
            System.out.println("Status Ikan Sekarang: " + chefFish.getState());


            System.out.println("\n=== 3. PLATING (MENYUSUN DI PIRING) ===");
            Dish myDish = new Dish();

            // Masukkan bahan ke Dish
            // Note: Dish.java kamu hanya menerima bahan jika ingredient.isReady() == true
            
            myDish.addComponents(chefRice); // Masuk (karena cooked)
            myDish.addComponents(chefFish); // Masuk (karena chopped)
            myDish.addComponents(chefNori); // Masuk (karena nori always true)

            System.out.println("Komponen di piring: ");
            for (Preparable p : myDish.getComponent()) {
                if (p instanceof Ingredient) {
                    System.out.println("- " + ((Ingredient)p).getName() + " [" + p.getState() + "]");
                }
            }


            System.out.println("\n=== 4. SERVING (MENYAJIKAN) ===");
            
            try {
                // Cek apakah dish ini sesuai dengan order yang ada
                int reward = orderManager.processServedDish(myDish);
                System.out.println("SUKSES! Dish disajikan.");
                System.out.println("Skor didapatkan: " + reward);
            } catch (OrderNotFoundException e) {
                System.out.println("GAGAL: Tidak ada order yang cocok!");
            } catch (InvalidDataException e) {
                System.out.println("ERROR: Data dish tidak valid.");
            }

            // Cek sisa order
            System.out.println("Sisa Order aktif: " + orderManager.getActiveOrders().size());


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}