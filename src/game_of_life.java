import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Random;

public class game_of_life implements ActionListener {
    int columns;
    int rows;
    static Cells [][] cellList;
    int squareSize = 10;
    JPanel panelCellGrid;
    JFrame frame;
    JPanel panelToolBar;
    JButton startButton;
    JButton randomPopulate;
    JComboBox<String>shapes;
    JSlider speed;
    JButton reset;
    boolean start = false;
    Integer genCount = 0;
    JLabel gCount;
    String suffix = "Generation Number : ";
    Font font = new Font("Serif", Font.ITALIC, 15);
    JLabel fast = new JLabel("Fast");
    JLabel slow = new JLabel("Slow ");



    game_of_life(int rows, int columns){
        this.rows = rows;
        this.columns = columns;
        cellList = new Cells[rows][columns];
        Dimension GridDimension = new Dimension(squareSize*rows, squareSize*rows);
        frame = new JFrame();
        panelCellGrid = new JPanel();
        panelCellGrid.setBackground(Color.GRAY);
        panelCellGrid.setPreferredSize(GridDimension);
        frame.setPreferredSize(GridDimension);

        String [] shapesList = {
                "Default",
                "Glider",
                "LightWeightSpaceShip",
                "MiddleWeightSpaceShip",
                "HeavyWeightSpaceShip",
                "GliderGun",
                "BigSpaceShip",
                "Pulsar",
                "DryLife"
        };

        shapes = new JComboBox<>(shapesList);
        shapes.setFont(font);
        shapes.setForeground(Color.lightGray);
        shapes.setBackground(Color.DARK_GRAY);
        reset = new JButton("Reset");
        JPanel panelSlider = new JPanel(new BorderLayout());
        Hashtable<Integer, JLabel> labelTable = new Hashtable<>();
        fast.setForeground(Color.lightGray);
        slow.setForeground(Color.lightGray);
        labelTable.put(60, fast);
        labelTable.put(1020, slow);
        speed = new JSlider(JSlider.HORIZONTAL, 60, 1020,60);
        speed.setMajorTickSpacing(120);
        speed.setLabelTable(labelTable);
        speed.setPaintTicks(true);
        speed.setBackground(Color.DARK_GRAY);
        speed.setForeground(Color.lightGray);
        speed.setPaintLabels(true);
        speed.setFont(font);
        speed.setPreferredSize(new Dimension(400,50));
        panelSlider.add(speed);
        reset.addActionListener(e -> {
            genCount = 0;
            gCount.setText(suffix+genCount);
            for (int i = 0; i <rows ;i++){
                for (int x = 0 ; x < columns; x++){
                    cellList[i][x].kill();
                }
            }
        });

        shapes.addActionListener(e -> {
            String name = (String)shapes.getSelectedItem();
            setNewShape(name);
        });

        panelToolBar= new JPanel();
        startButton = new JButton("start");
        gCount = new JLabel(suffix + genCount.toString());
        gCount.setForeground(Color.lightGray);
        gCount.setFont(font);
        startButton.addActionListener(e -> {
            if (start){
                start = false;
                startButton.setText("start");
            }else{
                start = true;
                updateThread();
                startButton.setText("stop");
            }
        });

        randomPopulate = new JButton("Populate");
        randomPopulate.addActionListener(e -> populateRandom(20));
        GridLayout grid = new GridLayout(columns,rows);
        frame.getContentPane().setLayout(new BorderLayout());
        int frameFactor  = 1080/rows;
        frame.add(panelToolBar,BorderLayout.NORTH);
        frame.add(panelCellGrid,BorderLayout.CENTER);
        frame.setSize(frameFactor*rows,frameFactor*rows+26);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        panelToolBar.setBackground(Color.DARK_GRAY);
        panelToolBar.add(panelSlider);
        panelToolBar.add(shapes);
        panelToolBar.add(startButton);
        panelToolBar.add(randomPopulate);
        panelToolBar.add(reset);
        panelToolBar.add(gCount);
        panelCellGrid.setLayout(grid);

        for (int i = 0; i<columns ; i++){
            for(int x = 0; x <rows; x++){
                Cells cell = new Cells(panelCellGrid,i,x,rows, columns);
                cellList[i][x] = cell;
            }
        }
        panelCellGrid.setBorder(BorderFactory.createLineBorder(Color.black));
    }

    public void updateThread() {
        Thread t = new Thread(()->{
            while (start) {
                genCount++;
                gCount.setText(suffix+genCount);
                int toChangeIndex;
                Cells[] cellsToBeChanged;
                Map<Integer[], Cells[]> indexAndList = getDeadAndLiveList();
                Map<Integer, Cells[]> cellsToBeChangedMap = getCellsToBeChanged(indexAndList);
                for (Map.Entry<Integer, Cells[]> entry : cellsToBeChangedMap.entrySet()) {
                    toChangeIndex = entry.getKey();
                    cellsToBeChanged = entry.getValue();
                    displayGens(toChangeIndex, cellsToBeChanged);
                }

            }
        });
        t.start();
    }

    public void setNewShape(String newShape){
        for (int i = 0; i <rows ;i++){
            for (int x = 0 ; x < columns; x++){
                cellList[i][x].setShape(newShape);
            }
        }
    }

    public void populateRandom(int percentageOfBoard){
        double g = 100.0;
        double d = percentageOfBoard /g;
        int cellNum = (int)(rows*columns*d);
        Cells [] toBeChanged = new Cells[cellNum];
        Random ran = new Random();
        for (int i = 0; i < cellNum ; i++){
            int iCord = ran.nextInt(columns);
            int xCord = ran.nextInt(rows);
            toBeChanged[i] = cellList[iCord][xCord];
        }
        for (Cells cell : toBeChanged){
            cell.killRevive();
        }
    }


    public Map<Integer, Cells[]>  getCellsToBeChanged (Map<Integer [],Cells[]> indexAndList){//doInBack
        Map <Integer,Cells[]> cellsToBeChangedMap = new HashMap<>();
        Integer toChangeIndex = 0;
        Cells [] cellsToBeChanged = new Cells[rows*columns];

        for (Map.Entry<Integer[], Cells[]> entry : indexAndList.entrySet()) {
            Integer [] key = entry.getKey();
            Cells [] value = entry.getValue();
            if (key[0] == 0){
                for(int i = 0; i < key[1];i++){
                    if(deadCellCheck(value[i].iIndex,value[i].xIndex)){
                        cellsToBeChanged[toChangeIndex] = cellList[value[i].iIndex][value[i].xIndex];
                        toChangeIndex++;
                    }
                }
            }else{
                for(int i = 0; i < key[1];i++){
                    if(liveCellCheck(value[i].iIndex,value[i].xIndex)){
                        cellsToBeChanged[toChangeIndex] = cellList[value[i].iIndex][value[i].xIndex];
                        toChangeIndex++;
                    }
                }
            }
        }
        cellsToBeChangedMap.put(toChangeIndex,cellsToBeChanged);
        return cellsToBeChangedMap;
    }

    public Map< Integer[] , Cells[] > getDeadAndLiveList(){
        Map<Integer[],Cells[]> indexAndList = new HashMap<>();
        Integer [] deadCellIDList = new Integer[2];
        deadCellIDList[0] = 0;
        Integer [] liveCellIDList = new Integer[2];
        liveCellIDList[0] = 1;
        Cells [] deadCellList = new Cells[rows*columns];
        Cells [] liveCellList = new Cells[rows*columns];
        int deadCellsIndex = 0;
        int liveCellsIndex = 0;
        for (int i = 0 ; i < columns ; i++){
            for (int x = 0; x<rows ; x++){
                if (cellList[i][x].isAlive){
                    liveCellList[liveCellsIndex] = cellList[i][x];
                    liveCellsIndex++;
                }else{
                    deadCellList[deadCellsIndex] = cellList[i][x];
                    deadCellsIndex++;
                }
            }
        }

        deadCellIDList[1] = deadCellsIndex;
        liveCellIDList[1] = liveCellsIndex;
        indexAndList.put(liveCellIDList,liveCellList);
        indexAndList.put(deadCellIDList,deadCellList);
        return indexAndList;
    }

    public void displayGens(int index, Cells [] cellsToBeChanged) {
        for (int f = 0; f < index; f++) {
            cellsToBeChanged[f].killRevive();

        }
        int delay = speed.getValue();
        stop(delay);
    }


    public void stop(long milli){
        try
        {
            Thread.sleep(milli);
        }
        catch (InterruptedException ex)
        {
            Thread.currentThread().interrupt();
        }
    }

    public Cells [] getSurroundingCells(int i ,int x){
        int cellListIndex = 0;
        Cells [] surroundingCells = new Cells[8];
        for (int c = 0; c < 8;c++){
            if(c == 0) {
                if (i - 1 >= 0) {
                    surroundingCells[cellListIndex] = cellList[i - 1][x];
                    cellListIndex++;
                }
            }else if (c == 1){
                if((i-1>=0)&&(x+1<rows)){
                    surroundingCells[cellListIndex] = cellList[i - 1][x + 1];
                    cellListIndex++;
                }
            }else if (c == 2){
                if(x+1<rows){
                    surroundingCells[cellListIndex] = cellList[i][x + 1];
                    cellListIndex++;
                }
            }else if (c == 3){
                if((i+1<rows)&&(x+1<rows)){
                    surroundingCells[cellListIndex] = cellList[i + 1][x + 1];
                    cellListIndex++;
                }
            }else if (c == 4){
                if(i+1<rows){
                    surroundingCells[cellListIndex] = cellList[i + 1][x];
                    cellListIndex++;
                }
            }else if (c == 5){
                if((i+1<rows)&&(x-1>=0)){
                    surroundingCells[cellListIndex] = cellList[i + 1][x - 1];
                    cellListIndex++;
                }
            }else if (c == 6){
                if ((x-1>=0)){
                    surroundingCells[cellListIndex] = cellList[i][x - 1];
                    cellListIndex++;
                }
            }else{
                if ((i-1>=0)&&(x-1>=0)){
                    surroundingCells[cellListIndex] = cellList[i - 1][x - 1];
                    cellListIndex++;
                }
            }
        }
        return surroundingCells;
    }

    public boolean deadCellCheck(int i , int x){
        boolean cellToChange = false;
        Cells [] surroundingCells = getSurroundingCells(i, x);
        int count = 0;
        for (Cells cell : surroundingCells){
            if (cell!= null){
                if (cell.isAlive){
                    count++;
                }
            }
        }
        if(count == 3){
            cellToChange = true;
        }
        return cellToChange;
    }

    public boolean liveCellCheck(int i , int x){
        boolean cellToChange;
        Cells [] surroundingCells = getSurroundingCells(i, x);
        int count =  0;
        for(Cells cell: surroundingCells){
            if (cell != null){
                if (cell.isAlive){
                    count++;
                }
            }
        }

        cellToChange = count != 2 && count != 3;

        return cellToChange;

    }

    public static void main(String[] args) {
        new game_of_life(100, 100);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    }
}