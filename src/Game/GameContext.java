package src.Game;

import src.Order.OrderManager;

public class GameContext {

    // Interface sederhana untuk kirim pesan
    public interface GameMessenger {
        void info(String msg);   // pesan normal / info
        void error(String msg);  // pesan gagal / warning
    }

    // Messenger default: kirim pesan ke console saja
    private static GameMessenger messenger = new GameMessenger() {
        @Override
        public void info(String msg) {
            System.out.println(msg);
        }

        @Override
        public void error(String msg) {
            System.err.println(msg);
        }
    };

    public static void setMessenger(GameMessenger m) {
        if (m != null) {
            messenger = m;
        }
    }

    public static GameMessenger getMessenger() {
        return messenger;
    }

    // === ORDER MANAGER GLOBAL ===
    // Supaya class lain bisa ambil order tanpa refresnsi kemana-mana
    private static OrderManager orderManager;
    public static OrderManager getOrderManager() { return orderManager; }
    public static void setOrderManager(OrderManager om) { orderManager = om; }

    private static MusicPlayer sfxPlayer = new MusicPlayer();

    public static void playSfx(String path, double volume) {
        if (sfxPlayer != null) {
            sfxPlayer.playOnce(path, volume);
        }
    }

}
