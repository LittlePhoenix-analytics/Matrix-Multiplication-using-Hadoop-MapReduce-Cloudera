# 📘 Matrix Multiplication using Hadoop MapReduce

## 🚀 Overview
This project demonstrates matrix multiplication using Hadoop MapReduce in Cloudera.

---

## ⚙️ Steps

# 1. Start services
sudo service cloudera-scm-server start
sudo service cloudera-scm-agent start

# 2. Create working folder
cd ~
mkdir matrix_mr
cd matrix_mr

# 3. Create code
nano MatrixMultiplication.java

# 4. Create input
nano matrix.txt

# 5. Compile
javac -classpath `hadoop classpath` -d . MatrixMultiplication.java

# 6. Create JAR
jar -cvf MatrixMultiplication.jar *

# 7. HDFS input
hdfs dfs -mkdir matrixinput
hdfs dfs -put matrix.txt matrixinput

# 8. Run job
hadoop jar MatrixMultiplication.jar MatrixMultiplication matrixinput matrixoutput

# 9. View output
hdfs dfs -cat matrixoutput/part-r-00000
