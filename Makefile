all:
	cd a_rmifs; make
	cd s_rmifs; make
	cd c_rmifs; make

clean:
	cd a_rmifs; make clean
	cd s_rmifs; make clean
	cd c_rmifs; make clean

