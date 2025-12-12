# 🍣 Nimonscooked — CLI Cooking Game (Java)

## Deskripsi Umum

**Nimonscooked** adalah permainan berbasis **Command Line Interface (CLI)** yang terinspirasi dari genre *time-management cooking game*. Pemain berperan sebagai chef yang bekerja sama di dapur untuk menyiapkan dan menyajikan hidangan sesuai pesanan pelanggan dalam batas waktu tertentu.

Game ini dikembangkan menggunakan **Java 8**, menerapkan prinsip **Object-Oriented Programming (OOP)**, **multithreading**, serta berbagai **design pattern** dan **SOLID principles**.


## Tujuan Permainan

Pemain harus:

- Menyelesaikan pesanan pelanggan dengan kombinasi bahan yang benar
- Mengelola waktu, peralatan dapur, dan posisi chef
- Mencapai target skor sebelum waktu habis
- Menghindari *failed orders* beruntun yang dapat menyebabkan stage gagal

---

## Fitur Utama

### 1. Gameplay Inti
- Gerak chef menggunakan **W / A / S / D**
- Interaksi dengan environment menggunakan **E**
- Dua chef yang dapat dikontrol dan di-*switch*
- Sistem skor, order, dan penalti
- Timer stage dan kondisi **PASS / FAIL**


### 2. Sistem Order
- Order di-*generate* secara dinamis
- Setiap order memiliki:
  - Resep (kombinasi ingredient)
  - Reward
  - Time limit
- Order kadaluarsa → penalti skor
- Terlalu banyak order gagal → stage langsung gagal

### 3. Station & Kitchen System
- **Cutting Station** → memotong ingredient
- **Cooking Station** → memasak ingredient
- **Washing Station** → mencuci plate
- **Ingredient Storage** → mengambil bahan
- **Plate Storage** → mengambil dan menyimpan piring
- **Serving Counter** → menyajikan hidangan

### 4. Plate & Kitchen Loop
- Plate memiliki state **clean / dirty**
- Setelah dish disajikan:
  - Plate menjadi kotor
  - Dikembalikan ke Plate Storage setelah **10 detik**
- Proses ini dikelola oleh **KitchenLoop** (background system)

### 5. Dash (Bonus Feature)
Chef dapat melakukan dash untuk bergerak cepat.

- Kombinasi tombol: **SHIFT + arah**
- Jarak dash: **3 tile**
- Cooldown: **2.5 detik**
- Tidak dapat dash saat chef sedang *busy*
- Validasi tabrakan dengan wall dan chef lain

### 6. Lempar Ingredient (Bonus Feature)
Chef dapat melempar ingredient mentah.

- Kombinasi tombol: **SHIFT + E**
- Jarak lempar: hingga **4 tile**
- Lemparan:
  - Terhenti sebelum wall
  - Dapat ditangkap oleh chef lain
  - Jika tidak tertangkap → jatuh ke lantai
- Tidak dapat melempar:
  - Plate
  - Dish
  - Kitchen utensils

---

## Arsitektur & Konsep Teknis

### OOP Concepts
- Inheritance
- Polymorphism
- Abstract Class & Interface
- Encapsulation
- Composition

### Design Patterns
- State Pattern → `GameState`
- Observer-like Pattern → update Order & Score
- Command-style Input Handling
- Singleton-like Context → `GameContext`

### SOLID Principles
- Single Responsibility Principle
- Open/Closed Principle
- Dependency Inversion (melalui `GameContext`)

### Concurrency
- Thread untuk:
  - Cutting
  - Cooking
  - Washing
  - KitchenLoop
- Menggunakan kontrol state dan flag `volatile`

---

## Struktur Folder
| Folder / File | Deskripsi | Link |
|--------------|----------|------|
| `src/Game/` | Core game logic | [Buka](src/Game/) |
| `src/chef/` | Chef movement & action | [Buka](src/chef/) |
| `src/Station/` | Kitchen station system | [Buka](src/Station/) |
| `src/Ingredients/` | Ingredients | [Buka](src/Ingredients/) |
| `src/Item/` | Item & utensils | [Buka](src/Item/) |
| `src/Order/` | Order & recipe system | [Buka](src/Order/) |
| `src/Exception/` | Custom exception | [Buka](src/Exception/) |
| `resources/assets/` | Visual & audio assets | [Buka](resources/assets/) |

---

## Cara Kompilasi & Menjalankan

### 1. Kompilasi
```bash
javac -d out src/**/*.java
```

### 2. Jalankan
```bash
java -cp out src.Game.Main
```

