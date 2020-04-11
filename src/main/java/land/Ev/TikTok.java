package land.Ev;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Created by CowardlyLion on 2020/1/12 22:47
 */
public class TikTok<T> {

    void aaa() {
        ArrayList<T> arr = new ArrayList<>();
        arr.sort(new Comparator<T>() {
            @Override
            public int compare(T o1, T o2) {
                return 0;
            }
        });


        T[] aaaa = null;
        Arrays.sort(aaaa, new Comparator<T>() {

            @Override
            public int compare(T o1, T o2) {
                return 0;
            }
        });

        Arrays.sort(aaaa);

    }
}
