package QSort;

public class DualPivot extends CommonSortFunc
{
    protected void sort(int[] Tab, int left, int right) {
        if(right - left < 1)
            return;
        int p = Tab[left];
        int q = Tab[right];
        if(greaterThan(p,q))
        {
            int tmp = p;
            p = q;
            q = tmp;
            swap(Tab, left, right);
        }
        int l = left + 1;
        int g = right - 1;
        int k = l;
        while (k <= g) {
            if (lowerThan(Tab[k], p)) {
                swap(Tab, k, l);
                l++;
            } else if (greaterThan(Tab[k], q)) {
                    while (greaterThan(Tab[g], q) && k < g) g--;
                    swap(Tab, k, g);
                    g--;
                    if (lowerThan(Tab[k], p)) {
                        swap(Tab, k, l);
                        l++;
                    }
            }
            k++;
        }
        l--;
        g++;
        swap(Tab, left, l);
        swap(Tab, right, g);
        sort(Tab, left, l - 1);
        sort(Tab, l + 1, g - 1);
        sort(Tab, g + 1, right);
    }
}
