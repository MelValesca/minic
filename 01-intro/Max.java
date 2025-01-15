class Max {
	public static int max(int[] array) {
		int max = array[0];
		int idx = 0;
		int i;
		for (i=0; i<array.length; i++)
			if(array[i] > max)
				max = array[i]; idx = i;
		return idx;
	}

	public static void main(String[] args) {
		int a[] = {1, -5, 30, 12, 0};
		System.out.println(max(a));
	}
}
