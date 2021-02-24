package de.kb1000.multiwindow.asm;

import net.devtech.grossfabrichacks.entrypoints.PrePrePreLaunch;
import net.devtech.grossfabrichacks.transformer.TransformerApi;
import net.devtech.grossfabrichacks.transformer.asm.AsmClassTransformer;
import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

public class MultiWindowASM implements PrePrePreLaunch, AsmClassTransformer {
    private final String screenName = FabricLoader.getInstance().getMappingResolver().mapClassName("intermediary", "net.minecraft.class_437").replace('.', '/');

    @Override
    public void onPrePrePreLaunch() {
        TransformerApi.registerPostMixinAsmClassTransformer(this);
    }

    @Override
    public boolean transform(ClassNode node) {
        if (node.name.startsWith("de/kb1000/multiwindow/asm/"))
            return false;

        boolean modified = false;
        if (ClassCache.extendsClass(node, screenName)) {
            for (MethodNode method : node.methods) {
                if (method.name.equals("<init>")) {
                    Type[] args = Type.getArgumentTypes(method.desc);
                    for (int i = 0; i < args.length; i++) {
                        if (args[i].getSort() == Type.OBJECT && args[i].getInternalName().equals(screenName)) {
                            InsnList insns = method.instructions;
                            AbstractInsnNode superCall = null;
                            for (AbstractInsnNode insn : insns) {
                                if (insn instanceof MethodInsnNode && insn.getOpcode() == Opcodes.INVOKESPECIAL && ((MethodInsnNode) insn).owner.equals(node.superName)) {
                                    superCall = insn;
                                    break;
                                }
                            }
                            if (superCall != null) {
                                modified = true;
                                insns.insert(superCall, new FieldInsnNode(Opcodes.PUTFIELD, screenName, "multi_window_parentScreen", "L" + screenName + ";"));
                                insns.insert(superCall, new VarInsnNode(Opcodes.ALOAD, i + 1));
                                insns.insert(superCall, new VarInsnNode(Opcodes.ALOAD, i));
                            }
                        }
                    }
                }
            }
        }

//        if (modified)
//        {
//            try {
//                ClassWriter writer = new ClassWriter(0);
//                Path classFile = Paths.get("mwcache/" + node.name + ".class");
//                Files.createDirectories(classFile.getParent());
//                node.accept(writer);
//                Files.write(classFile, writer.toByteArray());
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }

        return modified;
    }
}
