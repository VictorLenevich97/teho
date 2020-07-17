package by.varb.teho.utils;

public final class Calculation {
	
    //расчёт предполагаемого выхода из строя групп ВВСТ
    public static double [] calculateEquipmentFailure(int [] groups, double [] dailyAverageLoss, double kM) {
  		double[] wJ = new double [groups.length];
  		for (int i = 0; i < groups.length; i++) {
  			wJ[i] = (groups[i] * dailyAverageLoss[i]) / 100 * kM;
  		}
  		return wJ;
  	}
  	
  	//вычисление количества образцов ВВСТ, требующих ремонта(текущий и средний)
    public static double [][] сalculateEquipmentRequiringRepair(int[] qMinArray, int[] qMaxArray, double [] wJ, int [] qjMax) {
  		double[][] wjArray = new double[wJ.length][qMinArray.length];
          for (int i = 0; i < wJ.length; i++) 
          	for (int j = 0; j < qMinArray.length; j++) 
  	        {
  	        	wjArray[i][j] = Math.abs((wJ[i] * (Math.sin((Math.PI * qMaxArray[j]) / (2 * qjMax[i])) - Math.sin((Math.PI * qMinArray[j]) / (2 * qjMax[i])))));
  	        }
          return wjArray;
  	}
  	
  	//расчёт средней трудоёмкости ремонта ВВСТ
    public static double [][] calculateAverageComplexityRepair(double [][] wjArray, int[] qMaxArray) {
  		double[][] qjArray = new double[wjArray.length][wjArray[0].length];
  		for (int i = 0; i < qjArray.length; i++) 
  			for (int j = 0; j < qMaxArray.length; j++) 
  			{
  				qjArray[i][j] = 0.75 * wjArray[i][j] * qMaxArray[i];
  			}
  		return qjArray;
  	}
}
