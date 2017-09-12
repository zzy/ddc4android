package com.hmsoft.bluetooth.le;

public class COMMON {
	public static boolean valiDate(String data) {
		boolean result = false;
		String strNo1 = data.substring(0,22);	
    	String strNo2 = data.substring(22,24);
    	try{
    		if (Integer.parseInt(checkcode(strNo1), 16) == Integer.parseInt(strNo2, 16)) {
    			result = true;
    		}
    	}catch(Exception e){}
		return result;
		
	}
	
	public static String checkcode(String para){  
        String[] dateArr = new String[11];  
        try {  
            dateArr[0] = para.substring(0, 2);  
            dateArr[1] = para.substring(2, 4);  
            dateArr[2] = para.substring(4, 6);  
            dateArr[3] = para.substring(6, 8);  
            dateArr[4] = para.substring(8, 10); 
            dateArr[5] = para.substring(10, 12);
            dateArr[6] = para.substring(12, 14);
            dateArr[7] = para.substring(14, 16);
            dateArr[8] = para.substring(16, 18);
            dateArr[9] = para.substring(18, 20);
            dateArr[10] = para.substring(20, 22);
            
       } catch (Exception e) {  
           // TODO: handle exception  
       }  
       String code = "";  
       for (int i = 0; i < dateArr.length-1; i++) {  
           if(i == 0){  
               code = xor(dateArr[i], dateArr[i+1]);  
           }else{  
        	   code = xor(code, dateArr[i+1]);  
           }  
       }  
       return code.toUpperCase();  
    }
	
	private static String xor(String strHex_X,String strHex_Y){   
        //将x、y转成二进制形式   
        String anotherBinary=Integer.toBinaryString(Integer.valueOf(strHex_X,16));   
        String thisBinary=Integer.toBinaryString(Integer.valueOf(strHex_Y,16));   
        String result = "";   
        //判断是否为8位二进制，否则左补零   
        if(anotherBinary.length() != 8){   
        for (int i = anotherBinary.length(); i <8; i++) {   
        	anotherBinary = "0"+anotherBinary;   
            }   
        }   
        if( thisBinary.length() != 8){   
        for (int i =  thisBinary.length(); i <8; i++) {   
        	thisBinary = "0"+ thisBinary;   
            }   
        }   
        //异或运算   
        for(int i=0;i<anotherBinary.length();i++){   
        //如果相同位置数相同，则补0，否则补1   
                if( thisBinary.charAt(i)==anotherBinary.charAt(i))   
                    result+="0";   
                else{   
                    result+="1";   
                }   
            }  
    
        return Integer.toHexString(Integer.parseInt(result, 2));   
    }
}
