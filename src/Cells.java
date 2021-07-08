import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;



public class Cells implements ActionListener {
    int rows;
    int columns;
    JPanel panel;
    JButton cell;
    boolean isAlive = false;
    int iIndex;
    int xIndex;
    String shape;
    static Cells [][] cellList;




    public Cells(JPanel panel, int iIndex, int xIndex,int rows, int columns){
        this.panel = panel;
        this.iIndex = iIndex;
        this.xIndex = xIndex;
        this.cell = new JButton();
        this.shape = "Default";
        this.rows = rows;
        this.columns = columns;
        if(cellList == null){
            cellList = new Cells[rows][columns];
        }
        cellList[this.iIndex][this.xIndex] = this;
        cell.setBorderPainted(false);
        cell.addActionListener(this);
        cell.setBackground(Color.LIGHT_GRAY);
        panel.add(cell);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Shapes shape = new Shapes();
        switch (this.shape) {
            case "Default":
                deFault();
                break;
            case "Glider":
                if (!isAlive) {
                    shapeDecoder(this.iIndex,this.xIndex, shape.glider);
                }
                break;
            case "LightWeightSpaceShip":
                if (!isAlive) {
                    shapeDecoder(this.iIndex, this.xIndex,shape.lightWeightSpaceShip);
                }
                break;
            case "MiddleWeightSpaceShip":
                if (!isAlive){
                    shapeDecoder(this.iIndex,this.xIndex,shape.middleWeightSpaceShip);
                }
                break;
            case "HeavyWeightSpaceShip" :
                if (!isAlive){
                    shapeDecoder(this.iIndex,this.xIndex,shape.heavyWeightSpaceShip);
                }
                break;
            case "GliderGun" :
                if (!isAlive){
                    shapeDecoder(this.iIndex,this.xIndex,shape.gliderGun);
                }
                break;
            case "BigSpaceShip" :
                if (!isAlive){
                    shapeDecoder(this.iIndex,this.xIndex,shape.bigSpaceShip);
                }
                break;
            case "Pulsar" :
                if (!isAlive){
                    shapeDecoder(this.iIndex,this.xIndex,shape.pulsar);
                }
                break;
            case "DryLife" :
                if (!isAlive){
                    shapeDecoder(this.iIndex,this.xIndex,shape.dryLife);
                }
                break;

        }
    }

    public void setShape(String newShape){
        this.shape = newShape;
    }

    public void killRevive(){
        if (!isAlive) {
            cell.setBackground(Color.BLACK);
            isAlive = true;
        }else{
            cell.setBackground(Color.LIGHT_GRAY);
            isAlive = false;
        }
    }

    public void kill(){
        if(isAlive){
            cell.setBackground(Color.LIGHT_GRAY);
            isAlive = false;
        }
    }


    public void deFault(){
        isAlive = !isAlive;
        if (isAlive){
            cell.setBackground(Color.BLACK);

        }else{
            cell.setBackground(Color.LIGHT_GRAY);
        }
        System.out.println(this.shape);
    }


    static void shapeDecoder(int iIndex, int xIndex,int [][] shapeMap) {
        try {

        int columnNum = shapeMap.length;
        int rowNum = shapeMap[0].length;
        for (int i = 0; i < columnNum; i++) {
            for (int z = 0; z < rowNum; z++) {
                if (shapeMap[i][z] == 1) {
                    cellList[iIndex + i][xIndex + z].cell.setBackground(Color.BLACK);
                    cellList[iIndex + i][xIndex + z].isAlive = true;
                }
            }
        }
    }catch (ArrayIndexOutOfBoundsException e){
            System.out.println("shape too close to boundaries");

        }
    }
}
