package de.kb1000.multiwindow.asm;

import net.fabricmc.loader.launch.common.FabricLauncherBase;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ClassCache {
    private static final ConcurrentMap<String, ClassNode> cachedNodes = new ConcurrentHashMap<>();

    public static ClassNode loadClass(String name) {
        return cachedNodes.computeIfAbsent(name, name2 -> {
            try {
                byte[] bytes = FabricLauncherBase.getLauncher().getClassByteArray(name, true);
                ClassReader reader = new ClassReader(bytes);
                ClassNode node = new ClassNode();
                reader.accept(node, 0);
                return node;
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static boolean extendsClass(ClassNode current, String superClass) {
        if (current.superName.equals("java/lang/Object"))
            return false;

        return current.superName.equals(superClass) || extendsClass(loadClass(current.superName), superClass);
    }
}
