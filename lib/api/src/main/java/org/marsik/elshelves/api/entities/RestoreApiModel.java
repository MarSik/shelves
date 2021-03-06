package org.marsik.elshelves.api.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RestoreApiModel extends BackupApiModel {
	Set<ProjectApiModel> projects = new HashSet<>();
}
