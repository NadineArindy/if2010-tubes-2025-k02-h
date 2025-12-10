package src;

/**
 * WashingStation:
 *
 * - Hanya menerima dirty plate
 * - Proses washing membutuhkan waktu (durasi)
 * - Setelah durasi selesai → plate menjadi clean
 * - Chef yang memulai washing = busy
 * - Chef lain tetap bisa diswitch
 * - Interact (E) = mulai / melanjutkan proses washing
 * - Pickup/Drop (C) = ambil/taruh piring
 */
public class WashingStation extends Station {

    private boolean isProcessing = false;
    private int progress = 0;
    private final int requiredTime = 3;  // durasi mencuci

    private Plate dirtyPlate = null;

    public WashingStation(String id, Position position) {
        super(id, position);
    }

    // ========================================================================
    //                        PICKUP / DROP (C)
    // ========================================================================

    @Override
    public void handlePickupDrop(Chef chef) {
        Item inHand = chef.getInventory();

        // -----------------------------------------------
        // 1. Chef tangan kosong → ambil plate di station
        // -----------------------------------------------
        if (inHand == null) {

            // Ambil plate jika proses selesai
            if (dirtyPlate != null && dirtyPlate.isClean()) {
                chef.setInventory(dirtyPlate);
                dirtyPlate = null;
                resetProcess();
                return;
            }

            // Tidak ada plate yang bisa diambil
            return;
        }

        // -----------------------------------------------
        // 2. Chef membawa plate
        // -----------------------------------------------

        if (inHand instanceof Plate plate) {

            // hanya dirty plate yang boleh dicuci
            if (!plate.isClean() && dirtyPlate == null) {

                // taruh dirty plate di station
                dirtyPlate = plate;
                chef.setInventory(null);

                resetProcess();  // mulai fresh
                return;
            }

            // clean plate tidak bisa ditaruh di washing station
            return;
        }

        // selain plate → ignore
    }

    // ========================================================================
    //                         INTERACT (E)
    // ========================================================================

    @Override
    public void interact(Chef chef) {

        // tidak ada plate → tidak bisa mencuci
        if (dirtyPlate == null) return;

        // plate sudah bersih → tidak perlu dicuci
        if (dirtyPlate.isClean()) return;

        // mulai washing
        isProcessing = true;
        chef.setBusy(true);
        chef.assignWorkingStation(this);

        progress++;

        // selesai mencuci
        if (progress >= requiredTime) {
            dirtyPlate.clean();      // ubah jadi clean
            isProcessing = false;
            chef.setBusy(false);
        }
    }

    // ========================================================================
    //                             UPDATE LOOP
    // ========================================================================

    /**
     * update dipanggil oleh GameLoop() setiap frame.
     * Kalau kamu ingin washing otomatis lanjut tanpa tekan E terus:
     * 
     * cukup aktifkan logic ini:
     *
     * if (isProcessing) { progress++; }
     */
    @Override
    public void update(double deltaTime) {
        // default: progress hanya dari interact(E)
    }

    // ========================================================================
    //                              HELPERS
    // ========================================================================

    private void resetProcess() {
        progress = 0;
        isProcessing = false;
    }

    public boolean isProcessing() { return isProcessing; }
    public int getProgress() { return progress; }
}

