# Inventory
This application applies the following design patterns: Producer/Consumer and Singleton.

The package contains 9 Java files, 3 different input files for testing, and 1 ReadMe file.
It utilizes special functionalities of Java components, such as ArrayList, LinkedList, etc.
It can be enhanced and scaled easily if needed.

The 9 Java files and their purposes are listed below.
1. InventoryApp.java: the driver function. It creates 3 different kinds of threads: 
	dataSourceThread - the producer thread;
	inventoryAllocatorThread - the consumer thread;
	dbGeneratorThread - the simulate data generation thread.

2. DataSource.java: the producer thread; It handles requests, generates products and orders,
	and eventually publishes orders into the taskQueue if the taskQueue is not full. Once it publishes 
	the order into the taskQueue, it will notice other waiting threads.

3. InventoryAllocator.java: the consumer thread; It retrieves orders from the taskQueue if the 
	taskQueue is not empty. Once it gets an order, it processes the order and updates the data
	in the database simulator. If the inventory is zero, it produces an output list and halt.
	 
4. DBRunnable.java: the data generation thread; It is not a required feature. It helps to 
	demonstrate the special situation - all inventory is not zero anymore.
 
5. DataSourceParser.java: It provides useful methods to parse the inputs. 

6. InventoryDB.java: the database simulator. It uses the Lazy Initialize Singleton design pattern.

7. Order.java: each request is an order. It contains requestId, header, and a list of products.

8. Product.java: it contains product name and product quantity.

9. ProductSpec.java: it is a public enum. It makes much easier to add new product in the future.

There are 3 different input files.
1. InventoryInput.txt(the default input file): the provided regular inputs. 
	If this InventoryApp runs this input, it will generate the provided outputs. 
	
	The output is listed below.
	1:1,0,1,0,0::1,0,1,0,0::0,0,0,0,0
	2:0,0,0,0,5::0,0,0,0,0::0,0,0,0,5
	3:0,0,0,4,0::0,0,0,0,0::0,0,0,4,0
	4:1,0,1,0,0::1,0,0,0,0::0,0,1,0,0
	5:0,3,0,0,0::0,3,0,0,0::0,0,0,0,0
 
2. InvalidInventoryInput.txt: the provided invalid inputs. If this InventoryApp runs this input, 
	it will generate invalid request warnings base on different invalid cases. 

	The samples outputs are listed below.
	Invalid request! Quantity is 0 for only 1 product.
	Invalid request! Invalid product name: F
	Invalid request! Quantity is less than 0 or greater than 5.
	
3. SpecialInventoryInput.txt: when all inventory is zero, the system should halt 
	and produce output listing. Once the inventory is not zero anymore, 
	the system should be noticed and continue to process the rest requests 
	until all inventory is zero again. If this InventoryApp runs this input, 
	it will demonstrate the situation.
	
Run the InventoryApp system:
step #1: get the project from the GitHub.
	git clone https://github.com/ichingfong888/Inventory.git
	
step #2: Import this project into the Eclipse IDE.
	Import -> General -> Existing Project into Workspace -> click "Next" button
	
	click "Browse" button and choose the InventoryProject folder 
	which under the Inventory folder
	
step #3: Open InventoryApp.java and run it as a normal Java application.
	Run -> Run As -> Java Application
