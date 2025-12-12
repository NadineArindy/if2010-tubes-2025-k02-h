package src.Game;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class MenuPanel extends JPanel {

    private GameState state = GameState.MAIN_MENU;

    // info untuk stage select & result screen
    private boolean easyCleared = false;
    private boolean hardCleared = false;
    private String lastResultText = "";   // PASS / TIME UP / TOO MANY FAILED
    private int lastScore = 0;

    private int lastSuccessOrders = 0;
    private int lastFailedOrders  = 0;

    public void setState(GameState state) {
        this.state = state;
        repaint();
    }

    public GameState getState() {
        return state;
    }

    public void setStageProgress(boolean easyCleared, boolean hardCleared) {
        this.easyCleared = easyCleared;
        this.hardCleared = hardCleared;
    }

    // ditampilkan di result screen
    public void setLastResult(String resultText, int score, int successCount, int failedCount) {
        this.lastResultText = resultText;
        this.lastScore = score;
        this.lastSuccessOrders = successCount;
        this.lastFailedOrders  = failedCount;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // pilih gambar berdasarkan state
        switch (state) {
            case MAIN_MENU    -> drawMainMenu(g);
            case HELP         -> drawHelp(g);
            case STAGE_SELECT -> drawStageSelect(g);
            case POST_STAGE   -> drawResultScreen(g);
            default -> { /* IN_GAME digambar pada MapPanel */ }
        }
    }

    // ================= MAIN MENU =================
    private void drawMainMenu(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        // Desain main menu
        BufferedImage bg = AssetManager.menuMain;
        if (bg != null) {
            g2.drawImage(bg, 0, 0, getWidth(), getHeight(), null);
        } else {
            // fallback jika gambar belum ada
            g2.setColor(new Color(0x1E, 0x88, 0xE5));
            g2.fillRect(0, 0, getWidth(), getHeight());
        }

    }

    // ================= HELP =================
    private void drawHelp(Graphics g) {
        var g2 = (java.awt.Graphics2D) g;

        if (AssetManager.menuHelp != null) {
            g2.drawImage(AssetManager.menuHelp, 0, 0, getWidth(), getHeight(), null);
        }
    }

    // ================= STAGE SELECT =================
    private void drawStageSelect(Graphics g) {
        var g2 = (java.awt.Graphics2D) g;

        //pilih background sesuai progress
        if (!easyCleared && !hardCleared) {
            // NO CLEAR STAGE
            g2.drawImage(AssetManager.menuStage_00, 0, 0, getWidth(), getHeight(), null);
        } else if (easyCleared && !hardCleared) {
            // EASY STAGE CLEAR
            g2.drawImage(AssetManager.menuStage_10, 0, 0, getWidth(), getHeight(), null);
        } else if (!easyCleared && hardCleared) {
            // HARD STAGE CLEAR
            g2.drawImage(AssetManager.menuStage_01, 0, 0, getWidth(), getHeight(), null);
        } else {
            // ALL STAGES CLEAR
            g2.drawImage(AssetManager.menuStage_11, 0, 0, getWidth(), getHeight(), null);
        }
    }

    // ================= RESULT SCREEN =================
    private void drawResultScreen(Graphics g) {
        var g2 = (java.awt.Graphics2D) g;

        // pilih background sesuai status
        if ("PASS".equalsIgnoreCase(lastResultText)) {
            g2.drawImage(AssetManager.menuResultPass, 0, 0, getWidth(), getHeight(), null);
        } else if ("Time's Up".equalsIgnoreCase(lastResultText)) {
            g2.drawImage(AssetManager.menuResultTimeUp, 0, 0, getWidth(), getHeight(), null);
        } else {
            // Too Many Failed Orders 
            g2.drawImage(AssetManager.menuResultTooMany, 0, 0, getWidth(), getHeight(), null);
        }

        int cx = getWidth() / 2;
        int y = 300;  

        g.setFont(g.getFont().deriveFont(Font.BOLD, 24f));
        drawCentered(g, "Stage Result", cx, y);

        y += 28;
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 22f));
        drawCentered(g2, "Score  : " + lastScore, cx, y);

        y += 24;
        drawCentered(g2, "Order Success : " + lastSuccessOrders, cx, y);

        y += 24;
        drawCentered(g2, "Order Failed  : " + lastFailedOrders, cx, y);

        y += 40;  
        String message;
        if ("PASS".equalsIgnoreCase(lastResultText)) {
            message = "Great job, Chef! Customers are happy!";
        } else if (lastResultText.toLowerCase().contains("time")) {
            message = "Time's up... Try to be faster on the next service!";
        } else {
            // Too Many Failed Orders
            message = "Too many failed orders... Focus on one dish at a time!";
        } 

        g.setFont(g.getFont().deriveFont(Font.ITALIC, 16f));
        drawCentered(g, message, cx, y);
    }
    


    // ================= HELPER =================
    private void drawCentered(Graphics g, String text, int cx, int y) {
        int w = g.getFontMetrics().stringWidth(text);
        g.drawString(text, cx - w / 2, y);
    }
}
