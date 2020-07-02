class main {
    public static void main(String[] args){

        try{
            parts_example example = new parts_example();
            example.generateCSV();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
