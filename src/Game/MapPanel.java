package src.Game;

import java.awt.*;
import javax.swing.*;
import java.awt.image.BufferedImage;

import src.Item.Item;
import src.Order.Order;
import src.Order.OrderManager;
import src.Station.IngredientStorage;
import src.Station.Station;
import src.Station.WashingStation;
import src.chef.Chef;
import src.chef.Position;


class MapPanel extends JPanel {
    private final GameMap map;
    private Chef[] chefs;
    private Chef activeChef;
    private final ScoreManager scoreManager;
    private final OrderManager orderManager;
    private GameLoop gameLoop; 


    public MapPanel(GameMap map, Chef[] chefs, Chef activeChef, ScoreManager scoreManager, OrderManager orderManager) {
        this.map = map;
        this.chefs = chefs;
        this.activeChef = activeChef;
        this.scoreManager = scoreManager;
        this.orderManager = orderManager;

        // Warna background default panel
        setBackground(new Color(220, 220, 220));
    }

    public void setActiveChef(Chef chef) {
        this.activeChef = chef;
    }

    public void setChefs(Chef[] chefs) {
        this.chefs = chefs;
    }

    public void setGameLoop(GameLoop gameLoop) {
        this.gameLoop = gameLoop;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int ordersPanelWidth = 260;

        int cols = map.getWidth();
        int rows = map.getHeight();

        int availableWidth = getWidth() - ordersPanelWidth;

        // Hitung ukuran 1 tile supaya seluruh map muat di panel
        int cellSize = Math.min(availableWidth / cols, getHeight() / rows);
        int xOffset = (availableWidth - cols * cellSize) / 2;
        int yOffset = (getHeight() - rows * cellSize) / 2;

        // === BACKGROUND ASSETS ===
        BufferedImage stone = AssetManager.floor; 
        if (stone != null) {
            for (int y = 0; y < getHeight(); y += cellSize) {
                for (int x = 0; x < getWidth(); x += cellSize) {
                    g.drawImage(stone, x, y, cellSize, cellSize, null);
                }
            }
        } else {
            // fallback kalau icon background gagal load
            g.setColor(Color.DARK_GRAY);
            g.fillRect(0, 0, getWidth(), getHeight());
        }

        // === GAMBAR SETIAP TILE ===
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                Tile tile = map.getTileAt(x, y);

                int px = xOffset + x * cellSize;
                int py = yOffset + y * cellSize;

                // Pilih gambar dasar
                BufferedImage bg = null;

                if (tile.getType() == TileType.WALL) {
                    bg = AssetManager.wall;
                } else if (tile.getType() == TileType.FLOOR) {
                    bg = AssetManager.floor;
                } else if (tile.getType() == TileType.STATION) {
                    // Default lantai di bawah station
                    bg = AssetManager.floor;
                }

                // Gambar tile dasar
                if (bg != null) {
                    g.drawImage(bg, px, py, cellSize, cellSize, null);
                } else {
                    // fallback kalau gambar gagal
                    if (tile.getType() == TileType.WALL) {
                        g.setColor(Color.DARK_GRAY);
                    } else {
                        g.setColor(Color.LIGHT_GRAY);
                    }
                    g.fillRect(px, py, cellSize, cellSize);
                }


                // Border kotak tile
                g.setColor(Color.BLACK);
                g.drawRect(px, py, cellSize, cellSize);

                // ===== HIGHLIGHT TILE CHEF AKTIF =====
                if (activeChef != null && activeChef.getPosition() != null) {
                    Position ap = activeChef.getPosition();

                    // cek apakah tile ini posisi chef aktif
                    if (ap.getX() == x && ap.getY() == y) {
                        Graphics2D g2 = (Graphics2D) g;

                        // simpan warna 
                        Color oldColor = g2.getColor();
                        Stroke oldStroke = g2.getStroke();

                        // border tebal 
                        g2.setColor(Color.GREEN);
                        g2.setStroke(new BasicStroke(3f)); // 3px lebih tebal
                        g2.drawRect(px + 1, py + 1, cellSize - 2, cellSize - 2);

                        // kembali ke setting lama
                        g2.setColor(oldColor);
                        g2.setStroke(oldStroke);
                    }
                }
                // === GAMBAR STATION ===
                if (tile.hasStation()) {
                    Station s = tile.getStation();
                    BufferedImage baseIcon = null;

                    // IngredientStorage
                    if (s instanceof IngredientStorage storage) {
                            // Pakai icon sesuai jenis ingredient
                            baseIcon = AssetManager.getIngredientStorageIcon(storage.getIngredientClass());
                    } 
                    // Washing Station
                    else if (s instanceof WashingStation ws) {
                        Position basePos = ws.getPosition();
                        int baseX = basePos.getX();
                        int baseY = basePos.getY();

                        if (x == baseX && y == baseY) {
                            // tile W kiri 
                            baseIcon = AssetManager.washingRack;
                        } else {
                            // tile W kanan
                            baseIcon = AssetManager.washingSink;
                        }
                    }
                    else {
                        // Station lain pakai sprite berdasarkan simbol
                        baseIcon = switch (s.getSymbol()) {
                            case 'A' -> AssetManager.tileA;
                            case 'C' -> AssetManager.tileC;
                            case 'R' -> AssetManager.tileR;
                            case 'S' -> AssetManager.tileS;
                            case 'P' -> AssetManager.tileP;
                            case 'T' -> AssetManager.tileT;
                            case 'X' -> AssetManager.tileX;
                            default   -> null;
                        };
                    }

                    // Gambar icon dasar station
                    if (baseIcon != null) {
                        g.drawImage(baseIcon, px, py, cellSize, cellSize, null);
                    } else {
                        // fallback: kotak oranye di tengah tile
                        g.setColor(java.awt.Color.ORANGE);
                        g.fillRect(px + cellSize / 4, py + cellSize / 4,
                                cellSize / 2, cellSize / 2);
                    }

                    // ===== Cari item di atas station dan gambar iconnya =====
                    Item topItem = null;

                    if (s instanceof src.Station.Workstation ws && ws.hasItems()) {
                        topItem = ws.peekTopItem();
                    } else if (s instanceof src.Station.PlateStorage ps && !ps.isEmpty()) {
                        topItem = ps.peekPlate();
                    }

                    if (topItem != null) {
                        BufferedImage itemIcon = AssetManager.getItemIcon(topItem);
                        if (itemIcon != null) {
                            int margin = cellSize / 4;
                            g.drawImage(itemIcon,
                                    px + margin, py + margin,
                                    cellSize - 2 * margin, cellSize - 2 * margin,
                                    null);
                        }
                    }
                    
                    // === PROGRESS BAR STATION ===
                    float progress = s.getProgress();
                    if (progress >= 0f) {
                        if (s instanceof WashingStation ws) {
                            Position basePos = ws.getPosition();   // W kiri
                            int sinkX = basePos.getX() + 1;        // W kanan
                            int sinkY = basePos.getY();

                            // kalau tile yang sedang digambar bukan sink, SKIP bar
                            if (x != sinkX || y != sinkY) {
                                continue;
                            }
                        }

                        Graphics2D g2 = (Graphics2D) g;

                        int barWidth  = cellSize - cellSize / 4;
                        int barHeight = 6;

                        int barX = px + (cellSize - barWidth) / 2;
                        int barY = py + 2; // posisi sedikit di atas tile

                        // background bar
                        g2.setColor(new Color(230, 230, 230));
                        g2.fillRect(barX, barY, barWidth, barHeight);

                        // border bar
                        g2.setColor(Color.DARK_GRAY);
                        g2.drawRect(barX, barY, barWidth, barHeight);

                        // isi progress (0.0 - 1.0)
                        int filled = (int) (barWidth * Math.max(0f, Math.min(1f, progress)));
                        if (filled > 2) {
                            g2.setColor(new Color(76, 175, 80)); // hijau
                            g2.fillRect(barX + 1, barY + 1, filled - 2, barHeight - 2);
                        }
                    }
                } else {
                    // tile tanpa station, cek item di lantai
                    if (tile.hasGroundItem()) {
                        Item ground = tile.getGroundItem();
                        BufferedImage itemIcon = AssetManager.getItemIcon(ground);
                        if (itemIcon != null) {
                            int margin = cellSize / 4;
                            g.drawImage(
                                itemIcon,
                                px + margin, py + margin,
                                cellSize - 2 * margin, cellSize - 2 * margin,
                                null
                            );
                        }
                    }
                }
            }
            // ===== Gambar chef + item yang sedang dipegang =====
            if (chefs != null) {
                for (int i = 0; i < chefs.length; i++) {
                    Chef c = chefs[i];
                    if (c == null) continue;

                    Position pos = c.getPosition();
                    if (pos == null) continue;

                    int cx = xOffset + pos.getX() * cellSize;
                    int cy = yOffset + pos.getY() * cellSize;

                    // pilih sprite berdasarkan index (C1 = A, C2 = B)
                    BufferedImage sprite = (i == 0)
                            ? AssetManager.chef_A
                            : AssetManager.chef_B;

                    // Gambar sprite chef
                    if (sprite != null) {
                        g.drawImage(
                                sprite,
                                cx, cy,
                                cellSize, cellSize,
                                null
                        );
                    } else {
                        // fallback 
                        g.setColor(c == activeChef ? java.awt.Color.BLUE : java.awt.Color.GREEN);
                        g.fillOval(
                                cx + cellSize / 4,
                                cy + cellSize / 4,
                                cellSize / 2,
                                cellSize / 2
                        );
                    }

                    // Gambar icon item yang sedang dipegang di atas kepala chef
                    Item held = c.getInventory();
                    BufferedImage itemIcon = AssetManager.getItemIcon(held); 

                    if (itemIcon != null) {
                        int iconSize = cellSize / 2; // item lebih kecil dari chef
                        int ix = cx + (cellSize - iconSize) / 2;
                        int iy = cy - iconSize / 2; // item di atas kepala chef

                        g.drawImage(itemIcon, ix, iy, iconSize, iconSize, null);
                    }
                }
            }

            // HUD text di pojok kiri + panel order di kanan
            drawHUD(g);
            drawOrdersPanel(g);
        }
    }

    // Menggambar HUD sederhana di pojok kanan atas
    private void drawHUD(Graphics g) {
        g.setColor(Color.BLACK);
        g.setFont(g.getFont().deriveFont(Font.BOLD, 14f));

        // sejajar dengan panel ORDERS
        int cardWidth = 230;   
        int hudX = getWidth() - cardWidth - 15;
        int hudY = 20;

        // === Stage info ===
        if (gameLoop != null) {
            String stageName = gameLoop.getStageName();
            int timeLeft = gameLoop.getRemainingStageSeconds();

            g.drawString("Stage: " + stageName, hudX, hudY);
            hudY += 16;

            // format waktu mm:ss 
            int mm = timeLeft / 60;
            int ss = timeLeft % 60;
            String timeStr = String.format("Time: %02d:%02d", mm, ss);
            g.drawString(timeStr, hudX, hudY);
            hudY += 16;
        }

        // Score
        g.drawString("Score: " + scoreManager.getScore(), hudX, hudY);
        hudY += 16;

        // Jumlah order aktif
        g.drawString("Active Orders: " + orderManager.getActiveOrders().size(), hudX, hudY);
        hudY += 16;

    }

    // Menggambar panel kecil di sisi kanan layar untuk daftar order aktif
    private void drawOrdersPanel(Graphics g) {
        if (orderManager == null) return;

        java.util.List<Order> orders = orderManager.getActiveOrders();
        if (orders == null || orders.isEmpty()) return;

        int panelW = getWidth();
        int cardWidth  = 230;
        int cardX      = panelW - cardWidth - 15; // jarak 15px dari kanan
        int y          = 20 + 4 * 16 + 12;

        // === Header "ORDERS" ===
        g.setFont(g.getFont().deriveFont(Font.BOLD, 14f));
        g.setColor(new Color(30, 30, 30));                 // strip hitam
        g.fillRect(cardX, y, cardWidth, 24);
        g.setColor(Color.WHITE);
        g.drawString("ORDERS", cardX + 8, y + 16);

        y += 32; // geser posisi setelah header

        int maxShown = 3; // maksimal 3 order ditampilkan
        int shown = 0;

        for (Order o : orders) {
            if (o == null) continue;
            if (shown >= maxShown) break;

            java.util.List<src.Ingredients.Ingredient> comps = o.getRecipe().getRequiredIngredients();
            int ingredientLines = (comps != null) ? comps.size() : 0;

            int baseHeight = 45; // tinggi minimum (nama + time)
            int lineHeight = 14;
            int cardHeight = baseHeight + ingredientLines * lineHeight + 14;

            // === background card ===
            g.setColor(new Color(50, 50, 50));
            g.fillRect(cardX, y, cardWidth, cardHeight);

            g.setColor(new Color(250, 240, 200));
            g.fillRect(cardX + 2, y + 2, cardWidth - 4, cardHeight - 4);

            int textX = cardX + 8;
            int textY = y + 18;

            // Nama dish
            g.setColor(Color.BLACK);
            g.setFont(g.getFont().deriveFont(Font.BOLD, 13f));
            g.drawString(o.getRecipe().getName(), textX, textY);

            // Waktu + reward
            g.setFont(g.getFont().deriveFont(Font.PLAIN, 12f));
            textY += 16;
            int remain = o.getRemainingTime();
            int limit  = 100;
            int reward = o.getReward();

            g.drawString("Time: " + remain + "s   $" + reward, textX, textY);

            // Daftar ingredient resep
            if (comps != null) {
                for (src.Ingredients.Ingredient ing : comps) {
                    textY += lineHeight;
                    String line = "- " + ing.getName() + " (" + ing.getState() + ")";
                    g.drawString(line, textX, textY);
                }
            }

            // Progress bar waktu di bawah kartu
            int barX = cardX + 8;
            int barY = y + cardHeight - 10;
            int barW = cardWidth - 16;
            int barH = 5;

            g.setColor(new Color(220, 220, 220));
            g.fillRect(barX, barY, barW, barH);

            if (limit > 0) {
                double ratio = 1.0 - (double) remain / (double) limit;
                ratio = Math.max(0.0, Math.min(1.0, ratio));
                int filled = (int) (barW * ratio);

                g.setColor(Color.RED);
                g.fillRect(barX, barY, filled, barH);
            }

            y += cardHeight + 8;
            shown++;
        }
    }

}

