# TCP Experiment

​        TCP协议实验要求综合运用所学的TCP协议、数据结构和编程技能，运用Java模拟网络传输过程，由简到易完善实验代码，最终实现TCP传输。

​        TCP协议实验是复杂工程问题，因为该协议本身要解决一系列相互缠绕并相互制约的问题，包括传输过程中的位错、丢包、延迟、效率和拥塞控制。就复杂工程问题进行书面交流，是指能够将自己解决的复杂工程问题中的每一个关键环节进行技术说明，即结合原理、代码实现和实验评估进行说明。

**实验版本如下：**

(1) RDT2.0：有没有正确完成校验码函数

(2) RDT2.2：有没有正确完成出错处理的控制逻辑。

(3) RDT3.0：有没有正确完成丢包处理的控制逻辑。

(4) GB/SR/TCP：有没有正确完成滑动窗口的管理；GB关注累积确认；SR关注接收方有没有进行失序报文缓存；TCP同时关注累积确认、失序缓存以及发送方是否仅使用1个超时计时器。。

(5) TCP Tahoe：检查发送窗口有没有按照慢开始、拥塞避免增长，超时重发时有没有降到1；同时关注接收方发出的确认是否正确以及发送方是否仅使用1个超时计时器。

3）TCP Reno：检查有没有实现快重传、快恢复。