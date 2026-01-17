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
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Ringed_Changes {
	
	private static List<VersionInfo> cachedVersions = null;
	
	// Simple markdown patterns
	private static final Pattern ITALIC_PATTERN = Pattern.compile("(?<!\\*)\\*(?!\\*)(.+?)(?<!\\*)\\*(?!\\*)");
	private static final Pattern CODE_PATTERN = Pattern.compile("`(.+?)`");
	private static final Pattern LINK_PATTERN = Pattern.compile("\\[(.+?)\\]\\(.+?\\)");
	
	public static void addAllChanges(ArrayList<ChangeInfo> changeInfos) {
		List<VersionInfo> versions = getVersions();
		
		if (versions.isEmpty()) {
			// 如果解析失败，显示错误信息
			ChangeInfo error = new ChangeInfo("戒指地牢更新日志", true, "");
			error.hardlight(Window.TITLE_COLOR);
			changeInfos.add(error);
			
			ChangeInfo errorMsg = new ChangeInfo("无法加载更新日志", false, 
					"无法读取 UpdateLogs.md 文件。\n\n" +
					"请确保文件位于以下位置之一：\n" +
					"• assets/UpdateLogs.md\n" +
					"• docs/UpdateLogs.md\n" +
					"• 外部存储/UpdateLogs.md\n\n" +
					"提示：重启游戏后重试");
			errorMsg.hardlight(Window.SHPX_COLOR);
			changeInfos.add(errorMsg);
			return;
		}
		
		// 添加总标题
		ChangeInfo title = new ChangeInfo("戒指地牢更新日志", true, "");
		title.hardlight(Window.TITLE_COLOR);
		changeInfos.add(title);
		
		// 为每个版本创建 ChangeInfo
		for (VersionInfo version : versions) {
			addVersionDisplay(changeInfos, version);
			
			// 只在版本之间添加间距（除了最后一个版本）
			if (versions.indexOf(version) < versions.size() - 1) {
				ChangeInfo versionSeparator = new ChangeInfo("", false, "\n\n");
				versionSeparator.hardlight(Window.WHITE);
				changeInfos.add(versionSeparator);
			}
		}
	}
	
	/**
	 * 添加单个版本的显示信息
	 */
	private static void addVersionDisplay(ArrayList<ChangeInfo> changeInfos, VersionInfo version) {
		// 版本号作为主标题
		ChangeInfo versionInfo = new ChangeInfo(version.version, true, "");
		versionInfo.hardlight(Window.TITLE_COLOR);
		changeInfos.add(versionInfo);
		
		// 版本描述（如果有的话）- 使用更小的字体
		if (!version.title.isEmpty()) {
			ChangeInfo titleInfo = new ChangeInfo(version.title, false, "");
			titleInfo.hardlight(Window.SHPX_COLOR);
			changeInfos.add(titleInfo);
			
			// 添加描述后的间距
			ChangeInfo spacing = new ChangeInfo("", false, "");
			changeInfos.add(spacing);
		}
		
		// 每个章节的详细内容
		for (SectionContent section : version.sections) {
			addSectionDisplay(changeInfos, section);
		}
	}
	
	/**
	 * 添加单个章节的显示信息
	 */
	private static void addSectionDisplay(ArrayList<ChangeInfo> changeInfos, SectionContent section) {
		// 章节标题
		ChangeInfo sectionHeader = new ChangeInfo(section.title, true, "");
		sectionHeader.hardlight(getSectionColor(section.title));
		changeInfos.add(sectionHeader);
		
		// 章节内容
		String sectionContent = formatSectionContent(section);
		if (!sectionContent.isEmpty()) {
			ChangeInfo sectionInfo = new ChangeInfo("", false, sectionContent);
			sectionInfo.hardlight(Window.WHITE);
			changeInfos.add(sectionInfo);
		}
	}
	
	/**
	 * 根据章节类型获取对应的颜色
	 */
	private static int getSectionColor(String title) {
		if (title.contains("新内容") || title.contains("新增")) return 0x44FF44; // 绿色
		if (title.contains("平衡") || title.contains("调整")) return 0x44FFFF; // 青色
		if (title.contains("bug") || title.contains("修复")) return 0xFFA500; // 橙色
		if (title.contains("紧急")) return 0xFF4444; // 红色
		return Window.SHPX_COLOR; // 默认SPX色
	}
	
	private static List<VersionInfo> getVersions() {
		if (cachedVersions == null) {
			cachedVersions = parseUpdateLogs();
		}
		return cachedVersions;
	}
	
	// 简化的解析方法
	private static List<VersionInfo> parseUpdateLogs() {
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
						continue;
					} else if (!line.startsWith("##") && !line.startsWith("###")) {
						if (currentVersion != null) {
							currentVersion.title = line;
						}
						expectingVersionTitle = false;
						continue;
					} else {
						expectingVersionTitle = false;
					}
				}
				
				// 检测版本号 (## v0.1.5)
				if (line.startsWith("## v")) {
					String version = line.substring(3).trim();
					currentVersion = new VersionInfo(version, "");
					currentSection = null;
					versions.add(currentVersion);
					expectingVersionTitle = true;
					continue;
				}
				
				// 检测章节 (### 新内容, ### 平衡调整, ### bug修复)
				if (line.startsWith("### ")) {
					String sectionTitle = line.substring(4).trim();
					currentSection = new SectionContent(sectionTitle);
					currentVersion.sections.add(currentSection);
					continue;
				}
				
				// 添加到当前章节内容
				if (currentVersion != null && currentSection != null) {
					if (line.isEmpty()) {
						continue;
					}
					
					// 处理列表项
					if (originalLine.contains("- ")) {
						int dashIndex = originalLine.indexOf("- ");
						String item = originalLine.substring(dashIndex + 2).trim();
						item = processMarkdown(item);
						
						int indent = getIndentLevel(originalLine);
						if (indent == 0) {
							currentSection.items.add(item);
						} else if (indent == 2) {
							currentSection.items.add("  " + item);
						} else if (indent >= 4) {
							currentSection.items.add("    " + item);
						} else {
							currentSection.items.add(item);
						}
					} else {
						// 普通文本
						if (!currentSection.items.isEmpty()) {
							String lastItem = currentSection.items.get(currentSection.items.size() - 1);
							currentSection.items.set(currentSection.items.size() - 1, 
								lastItem + " " + processMarkdown(line));
						} else {
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
	
	// 格式化章节内容（优化层级显示和间距）
	private static String formatSectionContent(SectionContent section) {
		if (section == null || section.items.isEmpty()) {
			return "";
		}
		
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < section.items.size(); i++) {
			String item = section.items.get(i);
			
			if (item.startsWith("    ")) {
				// 三级或更深嵌套列表 - 8个空格缩进
				result.append("        _•_ ").append(item.trim()).append("\n");
			} else if (item.startsWith("  ")) {
				// 二级嵌套列表 - 4个空格缩进
				result.append("    _•_ ").append(item.trim()).append("\n");
			} else {
				// 一级列表 - 0个空格缩进
				if (i > 0) {
					result.append("\n"); // 在顶级item之间添加空行
				}
				result.append("_•_ ").append(item).append("\n");
			}
		}
		
		return result.toString().trim();
	}
	
	// 获取行的缩进级别
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
	
	// 简单的 markdown 处理
	private static String processMarkdown(String text) {
		if (text == null || text.isEmpty()) {
			return text;
		}
		
		// 处理斜体 *text*（但不是 **text**）
		text = ITALIC_PATTERN.matcher(text).replaceAll("$1");
		
		// 处理代码 `text`
		text = CODE_PATTERN.matcher(text).replaceAll("$1");
		
		// 处理链接 [text](url)
		text = LINK_PATTERN.matcher(text).replaceAll("$1");
		
		return text;
	}
	
	// 内部数据类
	private static class VersionInfo {
		String version;
		String title;
		List<SectionContent> sections;
		
		VersionInfo(String version, String title) {
			this.version = version;
			this.title = title;
			this.sections = new ArrayList<>();
		}
	}
	
	private static class SectionContent {
		String title;
		List<String> items;
		
		SectionContent(String title) {
			this.title = title;
			this.items = new ArrayList<>();
		}
	}
}
