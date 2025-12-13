# 🍣 NimonsCooked 
_Final Project – Object-Oriented Programming STI’24 K02-H_

![Screenshot 1](./resources/screenshots/ReadMe.png)


---

## 🎮 Game Description
**NimonsCooked** is a pixel-art, time-management cooking game inspired by *Overcooked*.  
Players control **two chefs** who must collaborate to prepare sushi dishes, manage kitchen stations, and complete customer orders under time pressure.

This project demonstrates the application of **Object-Oriented Programming (OOP)** principles, such as abstraction, inheritance, polymorphism, and encapsulation.

## 🧩 Main Features
- Two difficulty levels: **Easy** and **Hard**
- Dual-chef system with instant character switching
- Interactive kitchen stations (ingredients, cutting board, stove, serving area, etc.)
- Sushi recipe and processing system
- Time-based scoring and stage evaluation


## 🕹️ Controls

### Main Menu
| Key | Action |
|-----|--------|
| **X** | Start → Stage Select |
| **H** | Help |
| **Q** | Exit Game |

### Help Menu
| Key | Action |
|-----|--------|
| **ESC** | Back to Main Menu |

### Stage Select
| Key | Action |
|-----|--------|
| **1** | Start Easy Stage |
| **2** | Start Hard Stage |
| **ESC** | Back to Main Menu |

### In Game
| Key | Action |
|-----|--------|
| **W / A / S / D** | Move Chef |
| **E** | Interact (pick up, cut, cook, deliver) |
| **B** | Switch Chef |

### Post Stage
| Key | Action |
|-----|--------|
| **R** | Retry Stage |
| **N** | Back to Stage Select |

--- 

Game ini dikembangkan menggunakan *Java 16+, menerapkan prinsip **Object-Oriented Programming (OOP), 
**multithreading, serta berbagai **design pattern* dan *SOLID principles*.


## Tujuan Permainan

Pemain harus:

- Menyelesaikan pesanan pelanggan dengan kombinasi bahan yang benar
- Mengelola waktu, peralatan dapur, dan posisi chef
- Mencapai target skor sebelum waktu habis
- Menghindari failed orders beruntun yang dapat menyebabkan stage gagal

---

## Fitur Utama

### 1. Gameplay Inti
- Gerak chef menggunakan *W / A / S / D*
- Interaksi dengan environment menggunakan *E*
- Dua chef yang dapat dikontrol dan di-switch
- Sistem skor, order, dan penalti
- Timer stage dan kondisi *PASS / FAIL*


### 2. Sistem Order
- Order di-generate secara dinamis
- Setiap order memiliki:
  - Resep (kombinasi ingredient)
  - Reward
  - Time limit
- Order kadaluarsa → penalti skor
- Terlalu banyak order gagal → stage langsung gagal

### 3. Station & Kitchen System
- *Cutting Station* → memotong ingredient
- *Cooking Station* → memasak ingredient
- *Washing Station* → mencuci plate
- *Ingredient Storage* → mengambil bahan
- *Plate Storage* → mengambil dan menyimpan piring
- *Serving Counter* → menyajikan hidangan

### 4. Plate & Kitchen Loop
- Plate memiliki state *clean / dirty*
- Setelah dish disajikan:
  - Plate menjadi kotor
  - Dikembalikan ke Plate Storage setelah *10 detik*
- Proses ini dikelola oleh *KitchenLoop* (background system)

### 5. Dash (Bonus Feature)
Chef dapat melakukan dash untuk bergerak cepat.

- Kombinasi tombol: *SHIFT + arah*
- Jarak dash: *3 tile*
- Cooldown: *2.5 detik*
- Tidak dapat dash saat chef sedang busy
- Validasi tabrakan dengan wall dan chef lain

### 6. Lempar Ingredient (Bonus Feature)
Chef dapat melempar ingredient mentah.

- Kombinasi tombol: *SHIFT + E*
- Jarak lempar: hingga *4 tile*
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
- Generics
- Exceptions
- Collections
- Concurrency


### Design Patterns
- State Pattern → GameState
- Observer-like Pattern → update Order & Score
- Command-style Input Handling
- Singleton-like Context → GameContext

### SOLID Principles
- Single Responsibility Principle
- Open/Closed Principle
- Dependency Inversion (melalui GameContext)

### Concurrency
- Thread untuk:
  - Cutting
  - Cooking
  - Washing
  - KitchenLoop
- Menggunakan kontrol state dan flag volatile

---

## Struktur Folder
| Folder / File | Deskripsi | Link |
|--------------|----------|------|
| src/Game/ | Core game logic | [Buka](src/Game/) |
| src/chef/ | Chef movement & action | [Buka](src/chef/) |
| src/Station/ | Kitchen station system | [Buka](src/Station/) |
| src/Ingredients/ | Ingredients | [Buka](src/Ingredients/) |
| src/Item/ | Item & utensils | [Buka](src/Item/) |
| src/Order/ | Order & recipe system | [Buka](src/Order/) |
| src/Exception/ | Custom exception | [Buka](src/Exception/) |
| resources/assets/ | Visual & audio assets | [Buka](resources/assets/) |

---

## ▶️ How to Compile and Run the Game
Pastikan sedang berada di folder root project (yang berisi folder `src` dan `resources`).

---

### Compile and Run the Game via IDE 

1. Buka project ini di **IntelliJ / VS Code / Eclipse**.
2. Cari file `src/Game/Main.java`
3. Klik **Run `Main.main()`**
4. Game akan langsung berjalan.

---

### Compile and Run the Game via Terminal (Windows – PowerShell)

1. Buka terminal di folder project (ctrl + shift + ~).
2. Kompilasi semua file `.java` ke folder `out`:


```md
$files = Get-ChildItem -Recurse -Filter *.java | Select-Object -ExpandProperty FullName
javac -d out $files
java -cp out src.Game.Main



