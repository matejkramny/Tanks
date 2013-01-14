JAVA_BUILD_OPTS = -source 1.7 -target 1.7

.PHONY: build_core
build_core:
	mkdir -p build
	javac $(JAVA_BUILD_OPTS) -cp "../lwjgl/lwjgl.jar:../lwjgl/jinput.jar:../lwjgl/slick-util.jar:../lwjgl/lwjgl_util.jar" -d build `find me/matej/tanks -name \*.java`

.PHONY: core
core: build_core
	echo "Name: JPC Core" > core.manifest
	echo "Build-Date:" `date` >> core.manifest
	echo "" >> core.manifest
	jar -cf jpc_core.jar resources/bios/bios.bin resources/bios/vgabios.bin \
	    -C build org/jpc/classfile -C build org/jpc/emulator \
	    -C build org/jpc/support -C build org/jpc/j2se \
	    -C build org/jpc/ui \
		resources/smallpause.png \
		resources/smallplay.png
	rm -f core.manifest

.PHONY: application
application: build_core
	echo "Name: JPC Application" > application.manifest
	echo "Main-Class: me.matej.tanks.Main" >> application.manifest
	echo "Build-Date: " `date` >> application.manifest
	echo "" >> application.manifest

	jar -cfm Tanks.jar application.manifest \
	    me/matej/tanks/resources/SplashScreen.png \
		me/matej/tanks/resources/TankBody.png \
	    -C build me/matej/tanks
	rm -f application.manifest

.PHONY: clean
clean:
	rm -Rf build
	rm -f jpc_core.jar jpc.jar jpc.manifest application.jar \
	    application.manifest linuxapplication.jar linuxapplication.manifest \
	    applet.jar applet.manifest core.manifest

.PHONY: cleanse
cleanse: clean
	rm -f `find . -iname \*.class`
	rm -f `find . -name \*~ -o -name \*#`