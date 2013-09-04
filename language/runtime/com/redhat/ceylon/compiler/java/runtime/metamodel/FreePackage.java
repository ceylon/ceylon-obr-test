package com.redhat.ceylon.compiler.java.runtime.metamodel;

import java.util.List;

import ceylon.language.SequenceBuilder;
import ceylon.language.Sequential;
import ceylon.language.empty_;
import ceylon.language.model.Annotated$impl;
import ceylon.language.model.declaration.AnnotatedDeclaration$impl;
import ceylon.language.model.declaration.Declaration$impl;
import ceylon.language.model.declaration.Package$impl;

import com.redhat.ceylon.compiler.java.metadata.Ceylon;
import com.redhat.ceylon.compiler.java.metadata.Ignore;
import com.redhat.ceylon.compiler.java.metadata.Name;
import com.redhat.ceylon.compiler.java.metadata.TypeInfo;
import com.redhat.ceylon.compiler.java.metadata.TypeParameter;
import com.redhat.ceylon.compiler.java.metadata.TypeParameters;
import com.redhat.ceylon.compiler.java.runtime.model.ReifiedType;
import com.redhat.ceylon.compiler.java.runtime.model.TypeDescriptor;

@Ceylon(major = 5)
@com.redhat.ceylon.compiler.java.metadata.Class
public class FreePackage implements ceylon.language.model.declaration.Package, 
        AnnotationBearing,
        ReifiedType {

    @Ignore
    public static final TypeDescriptor $TypeDescriptor = TypeDescriptor.klass(FreePackage.class);
    
    private com.redhat.ceylon.compiler.typechecker.model.Package declaration;

    private FreeModule module;

    private Sequential<FreeTopLevelOrMemberDeclaration> members;

    @Override
    public String toString() {
        return declaration.getNameAsString();
    }
    
    public FreePackage(com.redhat.ceylon.compiler.typechecker.model.Package declaration){
        this.declaration = declaration;
    }
    
    @Override
    @Ignore
    public Declaration$impl $ceylon$language$model$declaration$Declaration$impl() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    @Ignore
    public AnnotatedDeclaration$impl $ceylon$language$model$declaration$AnnotatedDeclaration$impl() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    @Ignore
    public Package$impl $ceylon$language$model$declaration$Package$impl() {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    @Ignore
    public Annotated$impl $ceylon$language$model$Annotated$impl() {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    @Ignore
    public java.lang.annotation.Annotation[] $getJavaAnnotations() {
        Class<?> javaClass = Metamodel.getJavaClass(declaration);
        return javaClass != null ? javaClass.getAnnotations() : AnnotationBearing.NONE;
    }

    @Override
    @TypeInfo("ceylon.language::Sequential<Annotation>")
    @TypeParameters(@TypeParameter(value = "Annotation", satisfies = "ceylon.language.model::Annotation<Annotation>"))
    public <Annotation extends ceylon.language.model.Annotation<? extends Annotation>> Sequential<? extends Annotation> annotations(@Ignore TypeDescriptor $reifiedAnnotation) {
        return Metamodel.annotations($reifiedAnnotation, this);
    }

    @Override
    @TypeInfo("ceylon.language::String")
    public String getName() {
        return declaration.getNameAsString();
    }

    @Override
    public String getQualifiedName() {
        return getName();
    }

    @Override
    public ceylon.language.model.declaration.Module getContainer() {
        // this does not need to be thread-safe as Metamodel.getOrCreateMetamodel is thread-safe so if we
        // assign module twice we get the same result
        if(module == null){
            module = Metamodel.getOrCreateMetamodel(declaration.getModule());
        }
        return module;
    }

    @Override
    @TypeInfo("ceylon.language::Sequential<Kind>")
    @TypeParameters(@TypeParameter(value = "Kind", satisfies = "ceylon.language.model.declaration::TopLevelOrMemberDeclaration"))
    public <Kind extends ceylon.language.model.declaration.TopLevelOrMemberDeclaration> Sequential<? extends Kind> 
    members(@Ignore TypeDescriptor $reifiedKind) {
        
        Predicates.Predicate predicate = Predicates.isDeclarationOfKind($reifiedKind);
        
        return filteredMembers($reifiedKind, predicate);
    }

    @Override
    @TypeInfo("Kind")
    @TypeParameters(@TypeParameter(value = "Kind", satisfies = "ceylon.language.model.declaration::TopLevelOrMemberDeclaration"))
    public <Kind extends ceylon.language.model.declaration.TopLevelOrMemberDeclaration> Kind 
    getMember(@Ignore TypeDescriptor $reifiedKind, @Name("name") String name) {
        
        Predicates.Predicate predicate = Predicates.and(
                Predicates.isDeclarationNamed(name),
                Predicates.isDeclarationOfKind($reifiedKind)
        );
        
        return filteredMember($reifiedKind, predicate);
    }

    @Override
    @TypeInfo("ceylon.language::Sequential<Kind>")
    @TypeParameters({ 
        @TypeParameter(value = "Kind", satisfies = "ceylon.language.model.declaration::TopLevelOrMemberDeclaration"), 
        @TypeParameter(value = "Annotation") 
    })
    public <Kind extends ceylon.language.model.declaration.TopLevelOrMemberDeclaration, Annotation> Sequential<? extends Kind> 
    annotatedMembers(@Ignore TypeDescriptor $reifiedKind, @Ignore TypeDescriptor $reifiedAnnotation) {
        
        Predicates.Predicate predicate = Predicates.and(
                Predicates.isDeclarationOfKind($reifiedKind),
                Predicates.isDeclarationAnnotatedWith($reifiedAnnotation));
        
        return filteredMembers($reifiedKind, predicate);
    }

    private <Kind> Sequential<? extends Kind> filteredMembers(
            TypeDescriptor $reifiedKind,
            Predicates.Predicate predicate) {
        if (predicate == Predicates.false_()) {
            return (Sequential<? extends Kind>)empty_.$get();
        }
        List<com.redhat.ceylon.compiler.typechecker.model.Declaration> modelMembers = declaration.getMembers();
        SequenceBuilder<Kind> members = new SequenceBuilder<Kind>($reifiedKind, modelMembers.size());
        for(com.redhat.ceylon.compiler.typechecker.model.Declaration modelDecl : modelMembers){
            if (predicate.accept(modelDecl)) {
                Kind member = (Kind)Metamodel.getOrCreateMetamodel(modelDecl);
                members.append(member);
            }
        }
        return members.getSequence();
    }

    private <Kind> Kind filteredMember(
            TypeDescriptor $reifiedKind,
            Predicates.Predicate predicate) {
        if (predicate == Predicates.false_()) {
            return null;
        }
        List<com.redhat.ceylon.compiler.typechecker.model.Declaration> modelMembers = declaration.getMembers();
        for(com.redhat.ceylon.compiler.typechecker.model.Declaration modelDecl : modelMembers){
            if (predicate.accept(modelDecl)) {
                return (Kind)Metamodel.getOrCreateMetamodel(modelDecl);
            }
        }
        return null;
    }

    @Override
    @TypeInfo("ceylon.language.model.declaration::ValueDeclaration|ceylon.language::Null")
    public ceylon.language.model.declaration.ValueDeclaration getValue(String name) {
        com.redhat.ceylon.compiler.typechecker.model.Declaration toplevel = declaration.getMember(name, null, false);
        if(toplevel instanceof com.redhat.ceylon.compiler.typechecker.model.Value == false)
            return null;
        com.redhat.ceylon.compiler.typechecker.model.Value decl = (com.redhat.ceylon.compiler.typechecker.model.Value) toplevel;
        return (FreeAttribute) Metamodel.getOrCreateMetamodel(decl);
    }

    @Override
    @TypeInfo("ceylon.language.model.declaration::FunctionDeclaration|ceylon.language::Null")
    public ceylon.language.model.declaration.FunctionDeclaration getFunction(String name) {
        com.redhat.ceylon.compiler.typechecker.model.Declaration toplevel = declaration.getMember(name, null, false);
        if(toplevel instanceof com.redhat.ceylon.compiler.typechecker.model.Method == false)
            return null;
        com.redhat.ceylon.compiler.typechecker.model.Method decl = (com.redhat.ceylon.compiler.typechecker.model.Method) toplevel;
        return (FreeFunction) Metamodel.getOrCreateMetamodel(decl);
    }

    @Override
    @TypeInfo("ceylon.language.model.declaration::ClassOrInterfaceDeclaration|ceylon.language::Null")
    public ceylon.language.model.declaration.ClassOrInterfaceDeclaration getClassOrInterface(String name) {
        com.redhat.ceylon.compiler.typechecker.model.Declaration toplevel = declaration.getMember(name, null, false);
        if(toplevel instanceof com.redhat.ceylon.compiler.typechecker.model.ClassOrInterface == false)
            return null;
        com.redhat.ceylon.compiler.typechecker.model.ClassOrInterface decl = (com.redhat.ceylon.compiler.typechecker.model.ClassOrInterface) toplevel;
        return (FreeClassOrInterface) Metamodel.getOrCreateMetamodel(decl);
    }

    @Override
    @TypeInfo("ceylon.language.model.declaration::AliasDeclaration|ceylon.language::Null")
    public ceylon.language.model.declaration.AliasDeclaration getAlias(String name) {
        com.redhat.ceylon.compiler.typechecker.model.Declaration toplevel = declaration.getMember(name, null, false);
        if(toplevel instanceof com.redhat.ceylon.compiler.typechecker.model.TypeAlias == false)
            return null;
        com.redhat.ceylon.compiler.typechecker.model.TypeAlias decl = (com.redhat.ceylon.compiler.typechecker.model.TypeAlias) toplevel;
        return (FreeAliasDeclaration) Metamodel.getOrCreateMetamodel(decl);
    }

    @Override
    public boolean getShared() {
        return declaration.isShared();
    }

    @Ignore
    @Override
    public TypeDescriptor $getType() {
        return $TypeDescriptor;
    }

}
