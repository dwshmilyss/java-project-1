### 图的三种存储数据结构
- **邻接矩阵**
	
		顶点用一维数组 边或弧用二维数组存储
		优点：结构简单,操作方便
		缺点：对于稀疏图，这种实现方式将浪费大量的空间。

- **邻接表**

		顶点用一维数组 边或弧用链表存储。
		可以很方便的求出某个顶点的出度，但是求入度则需要遍历整个图。这时可以创建一个逆邻接表。
		该算法的时间复杂度为O(N+E) N为定点数 E为边数
		该结构适合存储稀疏图
		

- **十字交叉表** 

		将邻接表和逆邻接表相结合的存储方法，它解决了邻接表（或逆邻接表）的缺陷，即求入度（或出度）时必须遍历整个图。