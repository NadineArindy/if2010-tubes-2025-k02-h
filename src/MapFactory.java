package src;
class MapFactory {
    // CONTOH layout (14 kolom x 10 baris) – ganti dengan layout kelompokmu
    // setiap char: C,R,A,S,W,I,P,T,X,.,+,V sesuai legenda
    private static final String[] RAW_MAP = new String[]{
    "XXXXXXXXXARRRA",   // row 1 (14)
    "ACACACAC......A",  // row 2 (14)
    "A.....A.......A",  // row 3 (14)
    "I...V.A..V...I",   // row 4 (14)
    "I..A..A.......I",  // row 5 (14)
    "I..A..A.......A",  // row 6 (14)
    "S..A..A.......A",  // row 7 (14)
    "S..X..T..WWP.X",   // row 8 (14)
    "X.......X....X",   // row 9 (14)
    "XXXXXXXXXXXXXX"    // row 10 (14)
};



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

                if (c == 'X' || c == '+') {           // dinding / border
                    tile = new Tile(pos, TileType.WALL, false);
                } else if (c == '.' || c == 'V') {    // ruang jalan (dan spawn)
                    tile = new Tile(pos, TileType.FLOOR, true);
                } else {                              // station (C,R,A,S,W,I,P,T)
                    tile = new Tile(pos, TileType.STATION, false);
                    StationType st = StationType.fromSymbol(c);
                    if (st != null) {
                        Station station = new Station(st, pos);
                        tile.setStation(station);
                    }
                }

                if (c == 'V') {           // spawn chef
                    map.setSpawnPoint(pos);
                }

                map.setTile(x, y, tile);
            }
        }
        return map;
        
    }

    static {
    for (int i = 0; i < RAW_MAP.length; i++) {
        System.out.println("Row " + i + " length = " + RAW_MAP[i].length() + " : " + RAW_MAP[i]);
    }
}

}


