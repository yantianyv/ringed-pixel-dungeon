/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2025 Evan Debenham
* 
 * Ringed Pixel Dungeon
 * Copyright (C) 2025-2025 yantianyv
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.ui.changelist;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class UpdateLogsParser {
	
	public static class SectionContent {
		public String title;
		public List<String> items;
		
		public SectionContent(String title) {
			this.title = title;
			this.items = new ArrayList<>();
		}
	}
	
	public static class VersionInfo {
		public String version;
		public String title;
		public List<SectionContent> sectionsList;
		
		public VersionInfo(String version, String title) {
			this.version = version;
			this.title = title;
			this.sectionsList = new ArrayList<>();
		}
	}
	
	public static List<VersionInfo> parseUpdateLogs() {
		List<VersionInfo> versions = new ArrayList<>();
		
		try {
			// 尝试从 assets 目录读取
			FileHandle file = Gdx.files.internal("UpdateLogs.md");
			if (!file.exists()) {
				// 如果 assets 中不存在，尝试从外部文件读取
				file = Gdx.files.external("UpdateLogs.md");
			}
			if (!file.exists()) {
				// 尝试从项目根目录的 docs 目录读取
				file = Gdx.files.absolute("../docs/UpdateLogs.md");
			}
			if (!file.exists()) {
				// 最后尝试从当前工作目录的 docs 目录读取
				file = Gdx.files.absolute("docs/UpdateLogs.md");
			}
			
			if (!file.exists()) {
				// 如果都找不到，返回空列表
				return versions;
			}
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(file.read()));
			String line;
			VersionInfo currentVersion = null;
			SectionContent currentSection = null;
			boolean expectingVersionTitle = false;
			
			while ((line = reader.readLine()) != null) {
				String originalLine = line;
				line = line.trim();
				
				// 跳过特殊标题行
				if (line.startsWith("# <center>") || line.startsWith("<center>") || line.startsWith("<title>")) {
					continue;
				}
				
				// 如果正在等待版本标题
				if (expectingVersionTitle) {
					if (line.isEmpty()) {
						// 空行，继续等待
						continue;
					} else if (!line.startsWith("##") && !line.startsWith("###")) {
						// 这是版本标题
						if (currentVersion != null) {
							currentVersion.title = line;
						}
						expectingVersionTitle = false;
						continue;
					} else {
						// 下一个版本/章节，停止等待标题
						expectingVersionTitle = false;
					}
				}
				
				// 检测版本号 (## v0.1.5)
				if (line.startsWith("## v")) {
					String version = line.substring(3).trim();
					
					currentVersion = new VersionInfo(version, "");
					currentSection = null;
					versions.add(currentVersion);
					expectingVersionTitle = true; // 下一行可能是标题
					continue;
				}
				
				// 检测章节 (### 新内容, ### 平衡调整, ### bug修复)
				if (line.startsWith("### ")) {
					String sectionTitle = line.substring(4).trim();
					currentSection = new SectionContent(sectionTitle);
					currentVersion.sectionsList.add(currentSection);
					continue;
				}
				
				// 添加到当前章节内容
				if (currentVersion != null && currentSection != null) {
					if (line.isEmpty()) {
						// 空行，跳过
						continue;
					}
					
					// 处理列表项，正确解析层级（使用原始行来保持缩进）
					int indent = getIndentLevel(originalLine);
					if (originalLine.contains("- ")) {
						// 找到 "- " 的位置
						int dashIndex = originalLine.indexOf("- ");
						String item = originalLine.substring(dashIndex + 2).trim();
						item = processMarkdown(item);
						
						// 根据缩进级别添加适当的缩进标记
						if (indent == 0) {
							// 一级列表
							currentSection.items.add(item);
						} else if (indent == 2) {
							// 二级列表
							currentSection.items.add("  " + item);
						} else if (indent >= 4) {
							// 三级或更深列表
							currentSection.items.add("    " + item);
						} else {
							// 其他情况，作为一级列表
							currentSection.items.add(item);
						}
					} else {
						// 普通文本（可能是列表项的延续）
						if (!currentSection.items.isEmpty()) {
							// 追加到最后一个列表项
							String lastItem = currentSection.items.get(currentSection.items.size() - 1);
							currentSection.items.set(currentSection.items.size() - 1, 
								lastItem + " " + processMarkdown(line));
						} else {
							// 作为新项添加
							currentSection.items.add(processMarkdown(line));
						}
					}
				}
			}
			
			reader.close();
			
		} catch (IOException e) {
			// 如果读取失败，返回空列表
			e.printStackTrace();
		}
		
		return versions;
	}
	
	// 格式化章节内容为显示文本
	public static String formatSectionContent(SectionContent section) {
		if (section == null || section.items.isEmpty()) {
			return "";
		}
		
		StringBuilder result = new StringBuilder();
		for (String item : section.items) {
			// 判断是否是嵌套列表（以空格开头）
			if (item.startsWith("    ")) {
				// 三级或更深嵌套列表
				result.append("      _-_ ").append(item.trim()).append("\n");
			} else if (item.startsWith("  ")) {
				// 二级嵌套列表
				result.append("    _-_ ").append(item.trim()).append("\n");
			} else {
				// 一级列表
				result.append("_-_ ").append(item).append("\n");
			}
		}
		
		return result.toString().trim();
	}
	
	// 获取行的缩进级别（空格数）
	private static int getIndentLevel(String line) {
		int indent = 0;
		for (int i = 0; i < line.length(); i++) {
			if (line.charAt(i) == ' ') {
				indent++;
			} else {
				break;
			}
		}
		return indent;
	}
	
	// 简单的 markdown 处理：将 markdown 语法转换为游戏可显示的格式
	// 注意：**text** 会被保留，因为 RenderedTextBlock 支持用它来高亮文本
	private static String processMarkdown(String text) {
		if (text == null || text.isEmpty()) {
			return text;
		}
		
		// 处理斜体 *text*（但不是 **text**）-> text
		// 使用负向前瞻和负向后顾来避免匹配 **text**
		text = text.replaceAll("(?<!\\*)\\*(?!\\*)(.+?)(?<!\\*)\\*(?!\\*)", "$1");
		
		// 处理代码 `text` -> text
		text = text.replaceAll("`(.+?)`", "$1");
		
		// 处理链接 [text](url) -> text
		text = text.replaceAll("\\[(.+?)\\]\\(.+?\\)", "$1");
		
		return text;
	}
}
