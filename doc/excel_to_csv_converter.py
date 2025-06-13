#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Excel文件转CSV脚本
将Excel文件的每个工作表分别转换为独立的CSV文件
"""

import pandas as pd
import os
import sys
from pathlib import Path

def excel_to_csv(excel_file_path, output_dir="output"):
    """
    将Excel文件转换为CSV文件
    
    Args:
        excel_file_path (str): Excel文件路径
        output_dir (str): 输出目录
    """
    try:
        # 创建输出目录
        Path(output_dir).mkdir(exist_ok=True)
        
        # 获取文件名（不含扩展名）
        base_name = Path(excel_file_path).stem
        
        print(f"正在处理文件: {excel_file_path}")
        
        # 读取Excel文件的所有工作表
        excel_file = pd.ExcelFile(excel_file_path)
        
        print(f"发现工作表: {excel_file.sheet_names}")
        
        # 遍历每个工作表
        for sheet_name in excel_file.sheet_names:
            try:
                # 读取工作表数据
                df = pd.read_excel(excel_file_path, sheet_name=sheet_name)
                
                # 生成CSV文件名
                csv_filename = f"{base_name}_{sheet_name}.csv"
                csv_path = os.path.join(output_dir, csv_filename)
                
                # 保存为CSV文件，使用UTF-8编码
                df.to_csv(csv_path, index=False, encoding='utf-8-sig')
                
                print(f"✓ 成功转换工作表 '{sheet_name}' -> {csv_path}")
                print(f"  数据形状: {df.shape[0]} 行 × {df.shape[1]} 列")
                
            except Exception as e:
                print(f"✗ 转换工作表 '{sheet_name}' 时出错: {str(e)}")
        
        excel_file.close()
        print(f"完成处理文件: {excel_file_path}\n")
        
    except Exception as e:
        print(f"✗ 处理文件 {excel_file_path} 时出错: {str(e)}\n")

def main():
    """主函数"""
    # Excel文件路径
    excel_files = [
        "doc/理想测试用例_冷.xlsx",
        "doc/理想测试用例_热.xlsx"
    ]
    
    # 输出目录
    output_dir = "csv_output1"
    
    print("=" * 60)
    print("Excel文件转CSV转换器")
    print("=" * 60)
    
    # 检查文件是否存在
    for excel_file in excel_files:
        if not os.path.exists(excel_file):
            print(f"✗ 文件不存在: {excel_file}")
            continue
        
        # 转换文件
        excel_to_csv(excel_file, output_dir)
    
    print("=" * 60)
    print("转换完成！")
    print(f"所有CSV文件已保存到 '{output_dir}' 目录")
    print("=" * 60)

if __name__ == "__main__":
    # 检查是否安装了pandas
    try:
        import pandas as pd
        import openpyxl  # 用于读取xlsx文件
    except ImportError as e:
        print("错误: 缺少必要的依赖包")
        print("请安装: pip install pandas openpyxl")
        sys.exit(1)
    
    main() 