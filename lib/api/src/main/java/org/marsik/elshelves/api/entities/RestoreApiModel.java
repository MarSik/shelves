package org.marsik.elshelves.api.entities;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
public class RestoreApiModel extends BackupApiModel {
	Set<ProjectApiModel> projects;
}
