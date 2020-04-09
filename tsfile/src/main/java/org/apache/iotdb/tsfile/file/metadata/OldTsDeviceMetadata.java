/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.iotdb.tsfile.file.metadata;

import org.apache.iotdb.tsfile.utils.ReadWriteIOUtils;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OldTsDeviceMetadata {

  /**
   * start time for a device.
   **/
  private long startTime = Long.MAX_VALUE;

  /**
   * end time for a device.
   **/
  private long endTime = Long.MIN_VALUE;

  /**
   * Row groups in this file.
   */
  private List<OldChunkGroupMetaData> chunkGroupMetadataList = new ArrayList<>();

  public OldTsDeviceMetadata() {
    // allowed to clair an empty TsDeviceMetadata whose fields will be assigned later.
  }


  /**
   * deserialize from the given buffer.
   *
   * @param buffer -buffer to deserialize
   * @return -device meta data
   */
  public static OldTsDeviceMetadata deserializeFrom(ByteBuffer buffer) {
    OldTsDeviceMetadata deviceMetadata = new OldTsDeviceMetadata();

    deviceMetadata.startTime = ReadWriteIOUtils.readLong(buffer);
    deviceMetadata.endTime = ReadWriteIOUtils.readLong(buffer);

    int size = ReadWriteIOUtils.readInt(buffer);
    if (size > 0) {
      List<OldChunkGroupMetaData> chunkGroupMetaDataList = new ArrayList<>();
      for (int i = 0; i < size; i++) {
        chunkGroupMetaDataList.add(OldChunkGroupMetaData.deserializeFrom(buffer));
      }
      deviceMetadata.chunkGroupMetadataList = chunkGroupMetaDataList;
    }

    return deviceMetadata;
  }

  /**
   * add chunk group metadata to chunkGroups. THREAD NOT SAFE
   *
   * @param chunkGroup - chunk group metadata to add
   */
  public void addChunkGroupMetaData(OldChunkGroupMetaData chunkGroup) {
    chunkGroupMetadataList.add(chunkGroup);
    for (OldChunkMetadata chunkMetaData : chunkGroup.getChunkMetaDataList()) {
      // update startTime and endTime
      startTime = Long.min(startTime, chunkMetaData.getStartTime());
      endTime = Long.max(endTime, chunkMetaData.getEndTime());
    }
  }

  public List<OldChunkGroupMetaData> getChunkGroupMetaDataList() {
    return Collections.unmodifiableList(chunkGroupMetadataList);
  }

  public long getStartTime() {
    return startTime;
  }

  public long getEndTime() {
    return endTime;
  }

  @Override
  public String toString() {
    return "TsDeviceMetadata{" + " startTime=" + startTime
        + ", endTime="
        + endTime + ", chunkGroupMetadataList=" + chunkGroupMetadataList + '}';
  }

}
