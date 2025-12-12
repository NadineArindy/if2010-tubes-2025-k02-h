package src.Game;
import src.Station.*;
import src.chef.Position;
import src.Ingredients.*;
import src.Item.BoilingPot;
import src.Item.FryingPan;
import src.Item.Preparable;

class MapFactory {

    private static final String[] RAW_MAP = new String[]{
        "XXXXXXXXXARRRA",   // row 0 
        "ACACACA......A",   // row 1   
        "A.....A......A",   // row 2 
        "I...V.A..V...I",   // row 3 
        "I..A..A......I",   // row 4 
        "I..A..A......A",   // row 5 
        "S..A..A......A",   // row 6 
        "S..X..T..WWP.X",   // row 7 
        "X.......X....X",   // row 8 
        "XXXXXXXXXXXXXX"    // row 9 
    };

    // Membuat GameMap berdasarkan RAW_MAP
    public static GameMap createSampleMap() {
        int height = RAW_MAP.length;
        int width  = RAW_MAP[0].length();

        GameMap map = new GameMap(width, height);

        for (int y = 0; y < height; y++) {
            String row = RAW_MAP[y];
            for (int x = 0; x < width; x++) {
                char c = row.charAt(x);
                Position pos = new Position(x, y);

                Tile tile;

                if (c == 'X') {   
                    // Dinding atau border, tidak bisa dilewati        
                    tile = new Tile(pos, TileType.WALL, false);
                } else if (c == '.' || c == 'V') {   
                    // Lantai kosong yang bisa dilewati, sekaligus titik spawn
                    tile = new Tile(pos, TileType.FLOOR, true);
                } else {                              
                    // STATION (C,R,A,S,W,I,P,T)
                    tile = new Tile(pos, TileType.STATION, false);
                    StationType st = StationType.fromSymbol(c);
                    Station station = null;
                    if (st != null) { 
                        switch (st) {
                            case CUTTING:
                                station = new CuttingStation("Cutting" + x + y, pos, c, st, 1, 0);
                                break;
                            case COOKING:
                                CookingStation cs = new CookingStation("Cooking" + x + y, pos, c, st, 1, 0);

                                // baris 0 kolom 10 & 11 = BoilingPot, kolom 12 = FryingPan
                                if (y == 0 && x == 10) {          // (10, 0)
                                    cs.addItem(new BoilingPot("Boiling Pot 1"));
                                } else if (y == 0 && x == 11) {   // (11, 0)
                                    cs.addItem(new BoilingPot("Boiling Pot 2"));
                                } else if (y == 0 && x == 12) {   // (12, 0)
                                    cs.addItem(new FryingPan("Frying Pan"));
                                }

                                station = cs;
                                break;
                            case ASSEMBLY:
                                station = new AssemblyStation("Assembly" + x + y, pos, c, st, 1, 0);
                                break;
                            case SERVING:
                                station = new ServingCounter("Serving" + x + y, pos, c, st, null, null, null);
                                break;
                            case WASHING:
                                Station reuse = null;
                                if (x > 0) {
                                    Tile leftTile = map.getTileAt(x - 1, y);  
                                    if (leftTile != null && leftTile.hasStation()
                                            && leftTile.getStation() instanceof WashingStation) {
                                        reuse = leftTile.getStation();
                                    }
                                }

                                if (reuse != null) {
                                    // tile W (kanan) pakai station yang sama dengan W kiri
                                    station = reuse;
                                } else {
                                    // tile W (kiri) buat WashingStation baru
                                    station = new WashingStation("Washing" + x + y, pos, c, st);
                                }
                                break;
                            case INGREDIENT:
                                Class<? extends Preparable> ingClass = ingredientFor(x, y);
                                station = new IngredientStorage("Ingredient" + x + "_" + y, pos, c, st, ingClass, true);
                                break;
                            case PLATE:
                                station = new PlateStorage("Plate" + x + y, pos, c, st, 4); // 4 is max plates
                                break;
                            case TRASH:
                                station = new TrashStation("Trash" + x + y, pos, c, st);
                                break;
                            default:
                                break;
                        }

                        tile.setStation(station);
                    }
                }

                // Titik spawn chef
                if (c == 'V') {           
                    map.setSpawnPoint(pos);
                }

                // Set tile ke dalam map
                map.setTile(x, y, tile);
            }
        }
        return map;
        
    }

    // Menentukan letak ingredient apa yang ada di Ingredient Storage
    private static Class<? extends Preparable> ingredientFor(int x, int y) {
        // I paling atas kiri -> Beras
        if (x == 0 && y == 3) return Rice.class;

        // I paling atas kanan -> Nori
        if (x == 13 && y == 3) return Nori.class;

        // I tengah kiri -> Timun
        if (x == 0 && y == 4) return Cucumber.class;

        // I tengah kanan -> Ikan
        if (x == 13 && y == 4) return Fish.class;

        // I bawah kiri -> Udang
        if (x == 0 && y == 5) return Shrimp.class;

        // fallback 
        return Rice.class;
    }

}
