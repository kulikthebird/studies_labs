package QSort;

public class QuickSort extends CommonSortFunc
{
    protected void sort(int[] Tab, int left, int right)
    {
        if(right - left < 1)
            return;
        int p = Tab[right];
        int i = left;
        int j = right;
        do
        {
            do i++; while(lowerThan(Tab[i], p));
            do j--; while(greaterThan(Tab[j], p));
            if(greaterThan(j, i))
                swap(Tab, i, j);
        } while(j>i);
        swap(Tab, i, right);
        sort(Tab, left, i-1);
        sort(Tab, i+1, right);
    }
}
