package org.sonatype.nexus.plugins.terraform.internal.util.json;

import java.io.IOException;

import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.sonatype.nexus.plugins.terraform.internal.util.json.model.MirrorVersion;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;

public class MirrorVersionSerializer extends StdSerializer<MirrorVersion> {

  public MirrorVersionSerializer() {
    this(null);
  }

  public MirrorVersionSerializer(Class<MirrorVersion> t) {
    super(t);
  }

  @Override
  public void serialize(MirrorVersion value, JsonGenerator gen, SerializerProvider provider) throws IOException {
    gen.writeStartObject();
    gen.writeEndObject();
  }

}
