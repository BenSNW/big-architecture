package hx.dsal.text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;


public class DoubleArrayTrie {

	private int[] base;
    private int[] check;
    private int[] checkIfEnd;
    private int count = 1;
    public static final int defaultSize = 10;
    private Map<Character,Integer> maps = new HashMap<>();
    private int b = 1;

    public DoubleArrayTrie(int size) {
        if(size < 2) size = defaultSize;
        base = new int[size];
        base[1] = 1;
        check = new int[size];
        checkIfEnd = new int[size];
    }
    
    public void insert(Set<String> words){
        words.forEach(this::insert);
    }
  
    public void insert(String word){
        if (StringUtils.isBlank(word))
        	return ;
    	int pre_p = b;
        int cur_p = 0;
        char[] chars = word.toCharArray();
        for(int num = 0;num<chars.length;num++){
            char singleChar =chars[num];
            int code = code(singleChar);
            cur_p = base[pre_p] + code;
            if(cur_p > base.length-1){
                extendsArray();
            }
            if(check[cur_p] == 0&& base[cur_p] == 0){//无此数据
                check[cur_p] = pre_p;
                base[cur_p] = cur_p;
                pre_p = cur_p;
            }else if(check[cur_p] == pre_p){//找到数据
                if(base[cur_p] > 0 && checkIfEnd[cur_p] == 0){//若表中的此数据非为终止
                    if(chars.length>num+1){//若词也非终点
                        pre_p = cur_p;
                        continue;
                    }//若词是终点
                    break;
                }else{//若表中数据为终结
                    if(chars.length>num+1){//若词为非终点
                        pre_p = cur_p;
                        continue;
                    }
                    break;
                }
            }else{// 位置被占用需要跟换base[pre_p] 的值
                List<Integer> list = childList(pre_p);
                int origin_base = base[pre_p];
                list.add(code+origin_base);

                int avail_base = findAvailBase(list);
                list.remove(new Integer(code+origin_base));
                for(int i:list){
                    int temp1 = i;
                    int temp2 = avail_base+i;
                    base[temp2] = base[temp1];
                    check[temp2] = check[temp1];
                    checkIfEnd[temp2] = checkIfEnd[temp1];
                    if(checkIfEnd[temp1] != 1){
                        List<Integer> ls = childList(temp1);
                        ls.forEach(o->check[o] = temp2);
                    }
                    base[temp1] = 0;
                    check[temp1] = 0;
                    checkIfEnd[temp1] = 0;
                }
                base[pre_p] = avail_base+origin_base;
                cur_p = avail_base+code+origin_base;
                check[cur_p] = pre_p;
                base[cur_p] = cur_p;
                pre_p = cur_p;
            }
        }
        checkIfEnd[cur_p] = 1;
    }

    private void extendsArray(){
        base = Arrays.copyOf(base,base.length*2);
        check = Arrays.copyOf(check,check.length*2);
        checkIfEnd = Arrays.copyOf(checkIfEnd,checkIfEnd.length*2);
    }

    private int code(char ch){
        Integer i  =  maps.get(ch);
        return i != null ? i:compute(ch,count++);
    }
    
    private int compute(char c, int count) {
        if (!maps.containsKey(c)) {
            maps.put(c, count);
        }
        return count;
    }

    private List<Integer> childList(int pos){
        List<Integer> result = new ArrayList<>();
        for(int i = 0;i<check.length;i++){
            if(check[i] == pos) {
                result.add(i);
            }
        }
        return result;
    }

    private int findAvailBase(List<Integer> list){
        for(int i =1;;i++){
            boolean flag = true;
            for(int j:list){
                int cur = j+i;
                if(cur > base.length-1) extendsArray();
                if(base[cur] != 0 || check[cur] != 0){
                    flag = false;
                    break;
                }
            }
            if(flag){
                return i;
            }

        }
    }
    public void delete(String word){
        if (StringUtils.isBlank(word)) return;
    	int pre = b;
        int cur ;
        char[] chars = word.toCharArray();
        for(int i = 0 ;i<chars.length;i++){
            char ch = chars[i];
            if(maps.get(ch) == null) return;
            cur = base[pre] +maps.get(ch);
            if(check[cur] != pre) return;
            if(checkIfEnd[cur] == 1 && i+1 == chars.length){//匹配到值开始删除
                checkIfEnd[cur] = 0;
                deleteLoop(cur);
                return;
            }
            if(check[cur] == pre){
                pre = cur;
            }

        }
    }
    
    private void deleteLoop(int pos){
        List<Integer> ls = childList(pos);
        if(ls.size() == 0){
            int pre = check[pos];
            base[pos] = 0;
            check[pos] = 0;
            checkIfEnd[pos] = 0;
            deleteLoop(pre);
        }
    }

    public String match(String word){
        if(StringUtils.isBlank(word)) return null;
        StringBuilder result = new StringBuilder();
        int pre = b;
        int cur;
        int now = 0;
        char[] chars = word.toCharArray();
        for(int i = 0 ;i<chars.length;i++){
            char ch  = chars[i];
            result.append(ch);
            if(maps.get(ch) == null)
            {
                result.delete(0,result.length());
                pre = b;
                i= now != 0?now-1:i;
                now = 0;
                continue;
            }
            cur = base[pre]+ maps.get(ch);
            if(check.length <= cur) return null;
            if(check[cur] != pre) {
                result.delete(0,result.length());
                pre = b;
                i= now != 0?now-1:i;
                now = 0;
                continue;
            }
            if(checkIfEnd[cur] == 1){
                return result.toString();
            }
            if(i+1 == chars.length){
                return null;
            }
            if(pre == b){
                now = i+1;
            }
            if(check[cur] == pre){
                pre = cur;
            }
        }
        return null;
    }
}
