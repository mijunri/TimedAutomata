package dbm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ta.Clock;
import ta.TimeGuard;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class DBM {

    private List<Clock> clockList;
    private Value[][] matrix;

    public static DBM init(List<Clock> clockList){
        //n为时钟数量加一，需要一个零时钟
        int n = clockList.size()+1;
        //初始化DBM数组，每一个时钟的范围都是[0,+)
        Value[][] matrix = new Value[n][n];
        for(int i = 0; i < n; i++){
            for(int j = 0; j < n; j++){
                matrix[i][j] = new Value(0,true);
            }
        }
        for(int i = 1; i < n; i++){
            matrix[i][0] = new Value(Integer.MAX_VALUE,false);
        }
        return new DBM(clockList,matrix);
    }


    //Floyds 算法,求最小环
    public void canonical(){
        for(int k = 0; k < size(); k++){
            for(int i = 0; i < size(); i++){
                for(int j = 0; j < size(); j++){
                    Value v1 = matrix[i][j];
                    Value v = Value.add(matrix[i][k],matrix[k][j]);
                    if(matrix[i][j].compareTo(v) > 0){
                        matrix[i][j] = v;
                    }
                }
            }
        }
    }

    //up操作，取消DBM的上限约束
    public void up(){
        for(int i = 1; i <size();i++ ){
            matrix[i][0] = new Value(Integer.MAX_VALUE,false);
        }
    }

    //and操作，将DBM与某一个时钟约束求并集
    public void and(Clock c, TimeGuard timeGuard){
        //因为0表示零时钟，所以索引下标加一
        int index = clockList.indexOf(c)+1;
        Value right = new Value(timeGuard.getRight(),!timeGuard.isRightOpen());
        if(right.compareTo(matrix[index][0]) < 0){
            matrix[index][0] = right;
        }
        Value left = new Value(timeGuard.getLeft()*-1,!timeGuard.isLeftOpen());
        if(left.compareTo(matrix[0][index]) < 0){
            matrix[0][index] = left;
        }
    }

    //reset操作，对某一个时钟的值进行重置
    public void reset(Clock c){
        int index = clockList.indexOf(c)+1;
        for(int i = 0; i < size(); i++){
            matrix[index][i] = matrix[0][i];
            matrix[i][index] = matrix[i][0];
        }
    }

    public int size(){
        return clockList.size()+1;
    }

    //必须是canonical的才能判断
    public Boolean isConsistent(){
        for(int i = 0; i < size(); i++){
            if(matrix[i][i].compareTo(new Value(0,true)) < 0){
                return false;
            }
        }
        return true;
    }

    //DBM的包含判断，判断是否包含另外一个DBM的约束
    public boolean include(DBM dbm){
        for(int i = 0; i < size(); i++){
            for(int j = 0; j < size(); j++){
                if(matrix[i][j].compareTo(dbm.matrix[i][j]) < 0){
                    return false;
                }
            }
        }
        return true;
    }

//    @Override
//    public String toString(){
//        StringBuilder sb = new StringBuilder();
//        sb.append("the dbm matrix is:\n");
//        for(int i = 0; i < 3; i++){
//            for(int j = 0; j < 3; j++){
//                if(matrix[i][j].getValue() == Integer.MAX_VALUE){
//                    sb.append("∞").append("<").append(" \t");
//                }else {
//                    sb.append(matrix[i][j].getValue());
//                    if(matrix[i][j].isEqual()){
//                        sb.append(" <=");
//                    }else {
//                        sb.append(" <");
//                    }
//                    sb.append(" \t");
//                }
//            }
//            sb.append("\n");
//        }
//        return sb.toString();
//    }

    //做一个DBM的拷贝，避免污染数据
    public DBM copy(){
        Value[][] matrix1 = new Value[size()][size()];
        for(int i = 0; i < size(); i++){
            for(int j = 0; j < size(); j++){
                matrix1[i][j] = new Value(matrix[i][j].getValue(),matrix[i][j].isEqual());
            }
        }
        return new DBM(clockList,matrix1);
    }
}

