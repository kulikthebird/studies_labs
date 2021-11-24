package QSort;

public abstract class CommonSortFunc
{
    public int compares = 0;
    public int swaps = 0;

    void swap(int[] arr, int a, int b)
    {
        swaps++;
        int tmp = arr[a];
        arr[a] = arr[b];
        arr[b] = tmp;
    }

    boolean greaterThan(int a, int b)
    {
        compares++;
        return a > b;
    }

    boolean lowerThan(int a, int b)
    {
        compares++;
        return a < b;
    }

    public void sort(int[] Tab)
    {
        sort(Tab, 0, Tab.length-1);
    }

    abstract protected void sort(int[] Tab, int left, int right);
}
