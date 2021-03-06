/* Copyright 2018 The OpenTracing Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.opentracing.contrib.specialagent;

import java.util.Arrays;
import java.util.List;

/**
 * A {@link NamedFingerprint} that represents the fingerprint of a
 * {@code Class}.
 *
 * @author Seva Safris
 */
class ClassFingerprint extends NamedFingerprint<ClassFingerprint> {
  private static final long serialVersionUID = 3179458505281585557L;

  private final String superClass;
  private final String[] interfaces;
  private final ConstructorFingerprint[] constructors;
  private final MethodFingerprint[] methods;
  private final FieldFingerprint[] fields;

  /**
   * Creates a new {@code ClassFingerprint} for the specified parameters.
   *
   * @param name The name of the class.
   * @param superClass The name of the super class, or {@code null} for
   *          {@code java.lang.Object}.
   * @param interfaces The sorted array of interface type names.
   * @param constructors The sorted array of {@code ConstructorFingerprint}
   *          objects, or {@code null} if there are no constructors.
   * @param methods The sorted array of {@code MethodFingerprint} objects, or
   *          {@code null} if there are no methods.
   * @param fields The sorted array of {@code FieldFingerprint} objects, or
   *          {@code null} if there are no fields.
   */
  ClassFingerprint(final String name, final String superClass, final String[] interfaces, final List<ConstructorFingerprint> constructors, final List<MethodFingerprint> methods, final List<FieldFingerprint> fields) {
    super(name);
    this.superClass = superClass;
    this.interfaces = interfaces == null || interfaces.length == 0 ? null : AssembleUtil.sort(interfaces);
    this.constructors = constructors == null || constructors.size() == 0 ? null : AssembleUtil.sort(constructors.toArray(new ConstructorFingerprint[constructors.size()]));
    this.methods = methods == null || methods.size() == 0 ? null : AssembleUtil.sort(methods.toArray(new MethodFingerprint[methods.size()]));
    this.fields = fields == null || fields.size() == 0 ? null : AssembleUtil.sort(fields.toArray(new FieldFingerprint[fields.size()]));
  }

  /**
   * Returns the name of the super {@code Class}, or {@code null} if the super
   * class is {@code Object.class}.
   *
   * @return The name of the super {@code Class}, or {@code null} if the super
   *         class is {@code Object.class}.
   */
  String getSuperClass() {
    return this.superClass;
  }

  /**
   * @return The names of the interface types.
   */
  String[] getInterfaces() {
    return this.interfaces;
  }

  /**
   * @return An array of {@link FieldFingerprint} objects, or {@code null} if no
   *         fields are present in this fingerprint.
   */
  FieldFingerprint[] getFields() {
    return this.fields;
  }

  /**
   * @return An array of {@link ConstructorFingerprint} objects, or {@code null}
   *         if no constructors are present in this fingerprint.
   */
  ConstructorFingerprint[] getConstructors() {
    return this.constructors;
  }

  /**
   * @return An array of {@link MethodFingerprint} objects, or {@code null} if
   *         no methods are present in this fingerprint.
   */
  MethodFingerprint[] getMethods() {
    return this.methods;
  }

  /**
   * Tests if the specified {@code ClassFingerprint} is compatible with this
   * fingerprint. A fingerprint {@code a} is considered to be compatible with
   * fingerprint {@code b} if {@code a} contains all, or a subset of, field,
   * method, and constructor fingerprints in the fingerprint of {@code b}.
   *
   * @param o The fingerprint to check for compatibility with this fingerprint.
   * @return {@code true} if the specified {@code ClassFingerprint} is
   *         compatible with this fingerprint.
   */
  public boolean compatible(final ClassFingerprint o) {
    if (superClass == null ? o.superClass != null : o.superClass != null && !superClass.equals(o.superClass))
      return false;

    if (interfaces == null ? o.interfaces != null : o.interfaces != null && !AssembleUtil.containsAll(interfaces, o.interfaces))
      return false;

    if (constructors == null ? o.constructors != null : o.constructors != null && !AssembleUtil.containsAll(constructors, o.constructors))
      return false;

    if (methods == null ? o.methods != null : o.methods != null && !AssembleUtil.containsAll(methods, o.methods))
      return false;

    if (fields == null ? o.fields != null : o.fields != null && !AssembleUtil.containsAll(fields, o.fields))
      return false;

    return true;
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == this)
      return true;

    if (!(obj instanceof ClassFingerprint))
      return false;

    final ClassFingerprint that = (ClassFingerprint)obj;
    if (superClass != null ? !superClass.equals(that.superClass) : that.superClass != null)
      return false;

    if (constructors == null ? that.constructors != null : that.constructors == null || !Arrays.equals(constructors, that.constructors))
      return false;

    if (methods == null ? that.methods != null : that.methods == null || !Arrays.equals(methods, that.methods))
      return false;

    if (fields == null ? that.fields != null : that.fields == null || !Arrays.equals(fields, that.fields))
      return false;

    return true;
  }

  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append("class ").append(getName());
    if (superClass != null)
      builder.append(" extends ").append(superClass);

    if (interfaces != null)
      builder.append(" implements ").append(AssembleUtil.toString(interfaces, ", "));

    builder.append(" {\n");
    if (constructors != null)
      builder.append("  ").append(getName()).append(AssembleUtil.toString(constructors, "\n  " + getName())).append('\n');

    if (methods != null)
      builder.append("  ").append(AssembleUtil.toString(methods, "\n  ")).append('\n');

    if (fields != null)
      builder.append("  ").append(AssembleUtil.toString(fields, "\n  ")).append('\n');

    builder.append('}');
    return builder.toString();
  }
}